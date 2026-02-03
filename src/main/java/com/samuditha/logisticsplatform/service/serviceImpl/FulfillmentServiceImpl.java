package com.samuditha.logisticsplatform.service.serviceImpl;

import com.samuditha.logisticsplatform.dto.*;
import com.samuditha.logisticsplatform.entity.Fulfillment;
import com.samuditha.logisticsplatform.entity.Order;
import com.samuditha.logisticsplatform.repository.FulfillmentRepository;
import com.samuditha.logisticsplatform.repository.OrderRepository;
import com.samuditha.logisticsplatform.service.FulfillmentService;
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
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class FulfillmentServiceImpl implements FulfillmentService {

    private final FulfillmentRepository fulfillmentRepository;
    private final OrderRepository orderRepository;

    @Override
    public FulfillmentResponse createFulfillment(UUID orderId, FulfillmentCreateRequest req) {
        try {
            if (orderId == null) {
                throw new IllegalArgumentException("Order ID must not be null");
            }

            Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

            String externalId = req.getExternalFulfillmentId() == null ? null : req.getExternalFulfillmentId().trim();
            if (externalId == null || externalId.isBlank()) {
                throw new IllegalArgumentException("externalFulfillmentId is required");
            }

            boolean duplicate = fulfillmentRepository.existsByOrder_OrderIdAndExternalFulfillmentIdIgnoreCase(orderId, externalId);

            if (duplicate) {
                throw new IllegalArgumentException("Fulfillment already exists for this order");
            }
            if (req.getDeliveredAt() != null && req.getShippedAt() != null && req.getDeliveredAt().isBefore(req.getShippedAt())) {
                throw new IllegalArgumentException("deliveredAt must be after shippedAt");
            }
            Fulfillment f = new Fulfillment();
            f.setOrder(order);
            f.setExternalFulfillmentId(externalId);
            f.setCarrier(req.getCarrier().trim());
            f.setServiceLevel(req.getServiceLevel().trim());
            f.setShippedAt(req.getShippedAt());
            f.setDeliveredAt(req.getDeliveredAt());
            f.setFulfillmentStatus(req.getStatus());
            f.setCreatedAt(Instant.now());
            f.setUpdatedAt(Instant.now());

            Fulfillment saved = fulfillmentRepository.save(f);
            return FulfillmentResponse.from(saved);
        } catch (DataAccessException e) {
            throw new RuntimeException("Fulfillment creation Error");
        }
    }

    @Override
    public PagedFulfillmentResponse searchFulfillments(UUID orderId, Instant from, Instant to, Fulfillment.FulfillmentStatus status, String carrier,
                                                       Integer page, Integer size, String sort) {
        try {
            if (orderId == null) {
                throw new IllegalArgumentException("Order ID must not be null");
            }

            if (!orderRepository.existsById(orderId)) {
                throw new IllegalArgumentException("Order not found: " + orderId);
            }
            int pageNo = (page == null || page < 0) ? 0 : page;
            int pageSize = (size == null || size < 1) ? 50 : Math.min(size, 500);

            Sort sortObj = parseSort(sort);
            Pageable pageable = PageRequest.of(pageNo, pageSize, sortObj);

            Page<Fulfillment> result = fulfillmentRepository.searchFulfillments(orderId, from, to, status, carrier, pageable);

            return toPagedResponse(result);

        } catch (DataAccessException e) {
            throw new RuntimeException("Fulfillment retrieving Error");
        }
    }

    private Sort parseSort(String sort) {
        String s = (sort == null || sort.isBlank()) ? "updatedAt,desc" : sort.trim();
        String[] parts = s.split(",");
        String field = parts[0].trim();
        Sort.Direction dir = (parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim()))
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(dir, field);
    }

    private PagedFulfillmentResponse toPagedResponse(Page<Fulfillment> page) {
        List<FulfillmentResponse> data = page.getContent().stream()
                .map(this::toResponse)
                .toList();

        return PagedFulfillmentResponse.builder()
                .data(data)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .build();
    }

    private FulfillmentResponse toResponse(Fulfillment f) {
        return FulfillmentResponse.builder()
                .id(f.getFulfillmentId())
                .orderId(f.getOrder().getOrderId())
                .externalFulfillmentId(f.getExternalFulfillmentId())
                .status(f.getFulfillmentStatus())
                .carrier(f.getCarrier())
                .serviceLevel(f.getServiceLevel())
                .shippedAt(f.getShippedAt())
                .deliveredAt(f.getDeliveredAt())
                .createdAt(f.getCreatedAt())
                .updatedAt(f.getUpdatedAt())
                .build();
    }

    @Override
    public PagedFulfillmentResponse searchFulfillmentsByExternalFulfillmentId(UUID orderId, String externalFulfillmentId, Integer page, Integer size) {
        try {
            if (orderId == null) {
                throw new IllegalArgumentException("Order ID must not be null");
            }

            if (!orderRepository.existsById(orderId)) {
                throw new IllegalArgumentException("Order not found: " + orderId);
            }

            int pageNo = (page == null || page < 0) ? 0 : page;
            int pageSize = (size == null || size < 1) ? 50 : Math.min(size, 500);

            Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "updatedAt"));

            Page<Fulfillment> result = fulfillmentRepository.searchByExternalFulfillmentId(orderId, externalFulfillmentId, pageable);

            return toPagedResponse(result);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error while retrieving fulfillments", e);
        }
    }

    @Override
    public FulfillmentResponse getFulfillmentById(UUID orderId, UUID fulfillmentId) {
        try {
            if (orderId == null) {
                throw new IllegalArgumentException("Order ID must not be null");
            }
            if (fulfillmentId == null) {
                throw new IllegalArgumentException("Fulfillment ID must not be null");
            }

            if (!orderRepository.existsById(orderId)) {
                throw new IllegalArgumentException("Order not found: " + orderId);
            }

            Fulfillment fulfillment = fulfillmentRepository.findByFulfillmentIdAndOrder_OrderId(fulfillmentId, orderId).orElseThrow(() -> new IllegalArgumentException(
                    "Fulfillment not found for order: " + fulfillmentId));

            return mapToResponse(fulfillment);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error while retrieving fulfillments", e);
        }
    }

    private FulfillmentResponse mapToResponse(Fulfillment f) {
        return FulfillmentResponse.builder()
                .id(f.getFulfillmentId())
                .orderId(f.getOrder().getOrderId())
                .externalFulfillmentId(f.getExternalFulfillmentId())
                .status(f.getFulfillmentStatus())
                .carrier(f.getCarrier())
                .serviceLevel(f.getServiceLevel())
                .shippedAt(f.getShippedAt())
                .deliveredAt(f.getDeliveredAt())
                .createdAt(f.getCreatedAt())
                .updatedAt(f.getUpdatedAt())
                .build();
    }

    @Override
    public FulfillmentResponse updateFulfillment(UUID orderId, UUID fulfillmentId, FulfillmentUpdateRequest req) {
        try {
            if (orderId == null) throw new IllegalArgumentException("Order ID must not be null");
            if (fulfillmentId == null) throw new IllegalArgumentException("Fulfillment ID must not be null");

            if (!orderRepository.existsById(orderId)) {
                throw new IllegalArgumentException("Order not found: " + orderId);
            }

            Fulfillment fulfillment = fulfillmentRepository.findByFulfillmentIdAndOrder_OrderId(fulfillmentId, orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Fulfillment not found for order"));

            if (req.getExternalFulfillmentId() == null || req.getExternalFulfillmentId().isBlank()) {
                throw new IllegalArgumentException("externalFulfillmentId is required");
            }

            if (fulfillmentRepository.existsByOrder_OrderIdAndExternalFulfillmentIdIgnoreCaseAndFulfillmentIdNot(orderId, req.getExternalFulfillmentId(), fulfillmentId)) {
                throw new IllegalArgumentException("externalFulfillmentId already exists for this order");
            }

            fulfillment.setExternalFulfillmentId(req.getExternalFulfillmentId());
            fulfillment.setFulfillmentStatus(req.getStatus() == null ? Fulfillment.FulfillmentStatus.UNKNOWN : req.getStatus());

            fulfillment.setCarrier(req.getCarrier());
            fulfillment.setServiceLevel(req.getServiceLevel());

            fulfillment.setShippedAt(req.getShippedAt());
            fulfillment.setDeliveredAt(req.getDeliveredAt());
            fulfillment.setUpdatedAt(Instant.now());


            Fulfillment saved = fulfillmentRepository.save(fulfillment);
            return mapToResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Invalid fulfillment update");
        }
    }

    @Override
    public FulfillmentResponse patchFulfillment(UUID orderId, UUID fulfillmentId, FulfillmentPatchRequest req) {
        try {
            if (orderId == null) throw new IllegalArgumentException("Order ID must not be null");
            if (fulfillmentId == null) throw new IllegalArgumentException("Fulfillment ID must not be null");

            if (!orderRepository.existsById(orderId)) {
                throw new IllegalArgumentException("Order not found: " + orderId);
            }

            Fulfillment fulfillment = fulfillmentRepository.findByFulfillmentIdAndOrder_OrderId(fulfillmentId, orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Fulfillment not found for order"));

            patchIfNotBlank(req.getExternalFulfillmentId(), newVal -> {
                if (fulfillmentRepository.existsByOrder_OrderIdAndExternalFulfillmentIdIgnoreCaseAndFulfillmentIdNot(orderId, newVal, fulfillmentId)) {
                    throw new IllegalArgumentException("externalFulfillmentId already exists for this order");
                }
                fulfillment.setExternalFulfillmentId(newVal);
            });

            patchIfNotBlank(req.getCarrier(), fulfillment::setCarrier);
            patchIfNotBlank(req.getServiceLevel(), fulfillment::setServiceLevel);

            if (req.getStatus() != null) fulfillment.setFulfillmentStatus(req.getStatus());
            if (req.getShippedAt() != null) fulfillment.setShippedAt(req.getShippedAt());
            if (req.getDeliveredAt() != null) fulfillment.setDeliveredAt(req.getDeliveredAt());

            fulfillment.setUpdatedAt(Instant.now());
            Fulfillment saved = fulfillmentRepository.save(fulfillment);
            return mapToResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Invalid fulfillment patch request");
        }
    }

    private void patchIfNotBlank(String value, java.util.function.Consumer<String> setter) {
        if (value == null) return;
        String v = value.trim();
        if (v.isBlank()) return;
        setter.accept(v);
    }

    @Override
    public void deleteFulfillment(UUID orderId, UUID fulfillmentId) {
        try {
            if (orderId == null) {
                throw new IllegalArgumentException("Order ID must not be null");
            }

            if (fulfillmentId == null) {
                throw new IllegalArgumentException("Fulfillment ID must not be null");
            }

            Fulfillment fulfillment = fulfillmentRepository.findByFulfillmentIdAndOrder_OrderId(fulfillmentId, orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Fulfillment not found for this order"));

            fulfillmentRepository.delete(fulfillment);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Unable to delete fulfillment");
        }
    }


}
