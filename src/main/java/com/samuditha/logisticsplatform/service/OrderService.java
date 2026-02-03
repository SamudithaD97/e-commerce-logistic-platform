package com.samuditha.logisticsplatform.service;

import com.samuditha.logisticsplatform.dto.*;
import com.samuditha.logisticsplatform.entity.Order;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Instant;
import java.util.UUID;

public interface OrderService {
    OrderResponse createOrder(OrderCreateRequest orderCreateRequest);

    PagedOrderResponse searchOrders(UUID orgId, UUID websiteId, Instant from, Instant to,
                                    Integer page, Integer size, String sort,
                                    Order.OrderStatus status, Order.FinancialStatus financialStatus, Order.FulfillmentStatus fulfillmentStatus);

    PagedOrderResponse searchByOrderIdAndName(UUID orgId, UUID websiteId, String externalOrderId, String externalOrderNumber, Integer page, Integer size);

    OrderResponse getOrderById(UUID orderId);

    OrderResponse updateOrder(UUID orderId,OrderUpdateRequest request);

    OrderResponse patchOrder(UUID orderId, OrderPatchRequest request);

    void deleteOrder(UUID orderId);
}
