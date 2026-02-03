package com.samuditha.logisticsplatform.service.serviceImpl;

import com.samuditha.logisticsplatform.dto.*;
import com.samuditha.logisticsplatform.entity.Order;
import com.samuditha.logisticsplatform.entity.Store;
import com.samuditha.logisticsplatform.entity.Tenant;
import com.samuditha.logisticsplatform.repository.OrderRepository;
import com.samuditha.logisticsplatform.repository.StoreRepository;
import com.samuditha.logisticsplatform.repository.TenantRepository;
import com.samuditha.logisticsplatform.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final TenantRepository tenantRepository;
    private final StoreRepository storeRepository;

    @Override
    public OrderResponse createOrder(OrderCreateRequest orderCreateRequest) {
        try {
            Tenant tenant = tenantRepository.findByTenantId(orderCreateRequest.getOrgId());
            if (tenant == null) {
                throw new IllegalArgumentException("Organization not found: " + orderCreateRequest.getOrgId());
            }
            Store store = storeRepository.findOneWebsiteDetailsByWebsiteIdAndOrgId(orderCreateRequest.getWebsiteId(), orderCreateRequest.getOrgId());
            if (store == null) {
                throw new IllegalArgumentException("Website not found for organization");
            }
            Optional<Order> exitOrder = orderRepository.findByTenant_TenantIdAndStore_StoreIdAndExternalOrderId(
                    orderCreateRequest.getOrgId(),
                    orderCreateRequest.getWebsiteId(),
                    orderCreateRequest.getExternalOrderId());
            if (exitOrder.isPresent()) {
                throw new IllegalArgumentException("Order already exists");
            }

            Order order = new Order();
            order.setTenant(tenant);
            order.setStore(store);

            order.setExternalOrderId(orderCreateRequest.getExternalOrderId().trim());
            order.setExternalOrderNumber(orderCreateRequest.getExternalOrderNumber());
            order.setOrderStatus((orderCreateRequest.getStatus()));
            order.setFinancialStatus(orderCreateRequest.getFinancialStatus());
            order.setFulfillmentStatus((orderCreateRequest.getFulfillmentStatus()));
            order.setCustomerEmail(orderCreateRequest.getCustomerEmail());
            order.setOrderTotalAmount(orderCreateRequest.getOrderTotal());
            order.setCurrency(orderCreateRequest.getCurrency());
            order.setOrderCreatedAt(orderCreateRequest.getOrderCreatedAt());
            order.setOrderUpdatedAt(orderCreateRequest.getOrderUpdatedAt());

            // ingestedAt set by @PrePersist in entity (or set here)
            if (order.getIngestedAt() == null) {
                order.setIngestedAt(Instant.now());
            }

            Order saved = orderRepository.save(order);

            return OrderResponse.builder()
                    .id(saved.getOrderId())
                    .orgId(saved.getTenant().getTenantId())
                    .websiteId(saved.getStore().getStoreId())
                    .externalOrderId(saved.getExternalOrderId())
                    .externalOrderNumber(saved.getExternalOrderNumber())
                    .status(saved.getOrderStatus())
                    .financialStatus(saved.getFinancialStatus())
                    .fulfillmentStatus(saved.getFulfillmentStatus())
                    .customerEmail(saved.getCustomerEmail())
                    .orderTotal(saved.getOrderTotalAmount())
                    .currency(saved.getCurrency())
                    .orderCreatedAt(saved.getOrderCreatedAt())
                    .orderUpdatedAt(saved.getOrderUpdatedAt())
                    .ingestedAt(saved.getIngestedAt())
                    .orderCreatedAt(saved.getOrderCreatedAt())
                    .orderUpdatedAt(saved.getOrderUpdatedAt())
                    .message("Order created successfully")
                    .build();

        } catch (DataAccessException e) {
            log.error("DB error creating order orgId={}, websiteId={}, externalOrderId={}", orderCreateRequest.getOrgId(), orderCreateRequest.getWebsiteId(), orderCreateRequest.getExternalOrderId(), e);
            throw new RuntimeException("Database error while creating order", e);
        }
    }

    @Override
    public PagedOrderResponse searchOrders(UUID orgId, UUID websiteId, Instant from, Instant to, Integer page, Integer size, String sort,
                                           Order.OrderStatus status, Order.FinancialStatus financialStatus, Order.FulfillmentStatus fulfillmentStatus) {
        try {
            int pageNo = (page == null || page < 0) ? 0 : page;
            int pageSize = (size == null || size < 1) ? 50 : Math.min(size, 500);

            Sort sortObj = Sort.by(Sort.Direction.DESC, "orderUpdatedAt"); // default

            if (sort != null && !sort.isBlank()) {
                String[] parts = sort.split(",");
                String field = parts[0].trim();

                // map API fields to entity fields
                if (field.equalsIgnoreCase("updatedAt")) field = "orderUpdatedAt";
                if (field.equalsIgnoreCase("createdAt")) field = "orderCreatedAt";

                Sort.Direction dir = (parts.length > 1 && parts[1].trim().equalsIgnoreCase("asc"))
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

                sortObj = Sort.by(dir, field);
            }

            Pageable pageable = PageRequest.of(pageNo, pageSize, sortObj);

            if (from != null && to != null && from.isAfter(to)) {
                throw new IllegalArgumentException("'from' must be <= 'to'");
            }

            Page<Order> result = orderRepository.searchOrders(orgId, websiteId, from, to, status,
                    financialStatus, fulfillmentStatus, pageable);

            List<OrderResponse> data = result.getContent().stream()
                    .map(this::toResponse)
                    .toList();

            return PagedOrderResponse.builder()
                    .data(data)
                    .page(result.getNumber())
                    .size(result.getSize())
                    .totalElements(result.getTotalElements())
                    .totalPages(result.getTotalPages())
                    .hasNext(result.hasNext())
                    .build();

        } catch (DataAccessException e) {
            log.error("DB error searching orders orgId={}, websiteId={}", orgId, websiteId, e);
            throw new RuntimeException("Database error while searching orders", e);
        }
    }

    private OrderResponse toResponse(Order o) {
        return OrderResponse.builder()
                .id(o.getOrderId())
                .orgId(o.getTenant().getTenantId())
                .websiteId(o.getStore().getStoreId())
                .externalOrderId(o.getExternalOrderId())
                .externalOrderNumber(o.getExternalOrderNumber())
                .status(o.getOrderStatus())
                .financialStatus(o.getFinancialStatus())
                .fulfillmentStatus(o.getFulfillmentStatus())
                .customerEmail(o.getCustomerEmail())
                .orderTotal(o.getOrderTotalAmount())
                .currency(o.getCurrency())
                .orderCreatedAt(o.getOrderCreatedAt())
                .orderUpdatedAt(o.getOrderUpdatedAt())
                .build();
    }

    @Override
    public PagedOrderResponse searchByOrderIdAndName(UUID orgId, UUID websiteId, String externalOrderId, String externalOrderNumber, Integer page, Integer size) {
        try {
            int pageNo = (page == null || page < 0) ? 0 : page;
            int pageSize = (size == null || size < 1) ? 50 : Math.min(size, 500);
            Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "orderUpdatedAt"));
            if (orgId == null) {
                throw new IllegalArgumentException("Organization ID must not be null");
            }
            boolean orgExists = tenantRepository.existsById(orgId);
            if (!orgExists) {
                throw new IllegalArgumentException("Organization not found: " + orgId);
            }

            boolean websiteExistsForOrg = storeRepository.existsByStoreIdAndTenant_TenantId(websiteId, orgId);
            if (!websiteExistsForOrg) {
                throw new IllegalArgumentException("Website not found for organization");
            }

            String extId = (externalOrderId == null || externalOrderId.isBlank()) ? null : externalOrderId.trim();
            String extNo = (externalOrderNumber == null || externalOrderNumber.isBlank()) ? null : externalOrderNumber.trim();

            Page<Order> result = orderRepository.searchByOrderIdAndName(orgId, websiteId, extId, extNo, pageable);

            System.out.println(result);
            List<OrderResponse> data = result.getContent()
                    .stream()
                    .map(this::toOrderResponse)
                    .toList();

            return PagedOrderResponse.builder()
                    .data(data)
                    .page(result.getNumber())
                    .size(result.getSize())
                    .totalElements(result.getTotalElements())
                    .totalPages(result.getTotalPages())
                    .hasNext(result.hasNext())
                    .build();


        } catch (DataAccessException e) {
            log.error("DB error searching by order id/name orgId={}, websiteId={}", orgId, websiteId, e);
            throw new RuntimeException("Database error while searching orders", e);
        }
    }

    private OrderResponse toOrderResponse(Order o) {
        return OrderResponse.builder()
                .id(o.getOrderId())
                .orgId(o.getTenant().getTenantId())
                .websiteId(o.getStore().getStoreId())
                .externalOrderId(o.getExternalOrderId())
                .externalOrderNumber(o.getExternalOrderNumber())
                .status(o.getOrderStatus())
                .financialStatus(o.getFinancialStatus())
                .fulfillmentStatus(o.getFulfillmentStatus())
                .customerEmail(o.getCustomerEmail())
                .orderTotal(o.getOrderTotalAmount())
                .currency(o.getCurrency())
                .orderCreatedAt(o.getOrderCreatedAt())
                .orderUpdatedAt(o.getOrderUpdatedAt())
                .build();
    }

    @Override
    public OrderResponse getOrderById(UUID orderId) {
        try {
            if (orderId == null) {
                throw new IllegalArgumentException("Order ID must not be null");
            }

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

            return OrderResponse.builder()
                    .id(order.getOrderId())
                    .externalOrderId(order.getExternalOrderId())
                    .externalOrderNumber(order.getExternalOrderNumber())
                    .status(order.getOrderStatus())
                    .financialStatus(order.getFinancialStatus())
                    .fulfillmentStatus(order.getFulfillmentStatus())
                    .customerEmail(order.getCustomerEmail())
                    .orderTotal(order.getOrderTotalAmount())
                    .currency(order.getCurrency())
                    .orderCreatedAt(order.getOrderCreatedAt())
                    .orderUpdatedAt(order.getOrderUpdatedAt())
                    .build();
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while searching order", e);
        }
    }

    @Override
    public OrderResponse updateOrder(UUID orderId, OrderUpdateRequest req) {
        try {
            if (orderId == null) {
                throw new IllegalArgumentException("Order ID must not be null");
            }
            if (req == null) {
                throw new IllegalArgumentException("Request body must not be null");
            }
            if (req.getOrgId() == null) {
                throw new IllegalArgumentException("Organization ID must not be null");
            }
            if (req.getWebsiteId() == null) {
                throw new IllegalArgumentException("Website ID must not be null");
            }

            if (!tenantRepository.existsById(req.getOrgId())) {
                throw new IllegalArgumentException("Organization not found: " + req.getOrgId());
            }

            if (!storeRepository.existsByStoreIdAndTenant_TenantId(req.getWebsiteId(), req.getOrgId())) {
                throw new IllegalArgumentException("Website not found for organization");
            }

            if (orderRepository.existsByExternalOrderIdAndOrderIdNot(req.getExternalOrderId(), orderId)) {
                throw new IllegalArgumentException("External order Id is already exists");
            }

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

            boolean orderOrganizationAndWebsiteLink = orderRepository.existsByOrderIdAndTenant_TenantIdAndStore_StoreId(
                    orderId, req.getOrgId(), req.getWebsiteId()
            );
            if (!orderOrganizationAndWebsiteLink) {
                throw new IllegalArgumentException("Order does not belong to the provided organization/website");
            }


            order.setTenant(tenantRepository.findByTenantId(req.getOrgId()));
            order.setStore(storeRepository.findByStoreId(req.getWebsiteId()));
            order.setExternalOrderId(req.getExternalOrderId());
            order.setExternalOrderNumber(req.getExternalOrderNumber());
            order.setCustomerEmail(req.getCustomerEmail());
            order.setCurrency(req.getCurrency());

            order.setOrderStatus(req.getStatus());
            order.setFinancialStatus(req.getFinancialStatus());
            order.setFulfillmentStatus(req.getFulfillmentStatus());

            order.setOrderTotalAmount(req.getOrderTotal());
            order.setOrderCreatedAt(req.getOrderCreatedAt());
            order.setOrderUpdatedAt(req.getOrderUpdatedAt());

            Order saved = orderRepository.save(order);

            return OrderResponse.builder()
                    .id(saved.getOrderId())
                    .externalOrderId(saved.getExternalOrderId())
                    .externalOrderNumber(saved.getExternalOrderNumber())
                    .status(saved.getOrderStatus())
                    .financialStatus(saved.getFinancialStatus())
                    .fulfillmentStatus(saved.getFulfillmentStatus())
                    .customerEmail(saved.getCustomerEmail())
                    .orderTotal(saved.getOrderTotalAmount())
                    .currency(saved.getCurrency())
                    .orderCreatedAt(saved.getOrderCreatedAt())
                    .orderUpdatedAt(saved.getOrderUpdatedAt())
                    .orgId(saved.getTenant().getTenantId())
                    .websiteId(saved.getStore().getStoreId())
                    .build();
        } catch (DataAccessException e) {
            log.error("DB error updating orderId={}", orderId, e);
            throw new RuntimeException("Database error while updating order", e);
        }
    }

    @Override
    @Transactional
    public OrderResponse patchOrder(UUID orderId, OrderPatchRequest req){
        try{
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

            patchIfNotBlank(req.getExternalOrderId(), newExternalOrderId -> {
                boolean exists = orderRepository.existsByExternalOrderIdAndOrderIdNot(newExternalOrderId, orderId);
                if (exists) {
                    throw new IllegalArgumentException("External Order ID already exists");
                }
                order.setExternalOrderId(newExternalOrderId);
            });

            patchIfNotBlank(req.getExternalOrderNumber(), order::setExternalOrderNumber);
            patchIfNotBlank(req.getCustomerEmail(), order::setCustomerEmail);
            patchIfNotBlank(req.getCurrency(), order::setCurrency);

            if (req.getStatus() != null) order.setOrderStatus(req.getStatus());
            if (req.getFinancialStatus() != null) order.setFinancialStatus(req.getFinancialStatus());
            if (req.getFulfillmentStatus() != null) order.setFulfillmentStatus(req.getFulfillmentStatus());
            if (req.getOrderTotal() != null) order.setOrderTotalAmount(req.getOrderTotal());
            if (req.getOrderCreatedAt() != null) order.setOrderCreatedAt(req.getOrderCreatedAt());
            if (req.getOrderUpdatedAt() != null) order.setOrderUpdatedAt(req.getOrderUpdatedAt());

            Order saved = orderRepository.save(order);
            return mapToResponse(saved);
        }catch (DataIntegrityViolationException e) {
            log.warn("Constraint violation patching orderId={}", orderId, e);
            throw new IllegalArgumentException("Invalid update request (constraint violation)");
        }
    }
    private void patchIfNotBlank(String value, Consumer<String> setter) {
        if (value != null && !value.trim().isEmpty()) {
            setter.accept(value.trim());
        }
    }
    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getOrderId())
                .orgId(order.getTenant().getTenantId())
                .websiteId(order.getStore().getStoreId())
                .externalOrderId(order.getExternalOrderId())
                .externalOrderNumber(order.getExternalOrderNumber())
                .status(order.getOrderStatus())
                .financialStatus(order.getFinancialStatus())
                .fulfillmentStatus(order.getFulfillmentStatus())
                .customerEmail(order.getCustomerEmail())
                .orderTotal(order.getOrderTotalAmount())
                .currency(order.getCurrency())
                .orderCreatedAt(order.getOrderCreatedAt())
                .orderUpdatedAt(order.getOrderUpdatedAt())
                .build();
    }

    @Override
    public void deleteOrder(UUID orderId) {
        try {
            if (orderId == null) {
                throw new IllegalArgumentException("Order ID must not be null");
            }

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

            orderRepository.delete(order);

        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Unable to delete order due to related records");
        }
    }



}
