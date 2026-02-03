package com.samuditha.logisticsplatform.controller;

import com.samuditha.logisticsplatform.dto.*;
import com.samuditha.logisticsplatform.entity.Order;
import com.samuditha.logisticsplatform.service.OrderService;
import com.samuditha.logisticsplatform.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;


@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/orders")
public class OrderController {

    private final OrderService orderService;
    private static final Logger log = LoggerFactory.getLogger(TenantController.class);

    // create organization
    @PostMapping(value = "")
    public OrderResponse createOrder(@RequestBody OrderCreateRequest orderCreateRequest) {
        log.info("Request to create order {}", orderCreateRequest);
        return orderService.createOrder(orderCreateRequest);
    }

    // search orders (date range + pagination)
    @GetMapping(value = "")
    public PagedOrderResponse searchOrders(
            @RequestParam UUID orgId,
            @RequestParam UUID websiteId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,

            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "50") Integer size,
            @RequestParam(defaultValue = "updatedAt,desc") String sort,
            @RequestParam(required = false) Order.OrderStatus status,
            @RequestParam(required = false) Order.FinancialStatus financialStatus,
            @RequestParam(required = false) Order.FulfillmentStatus fulfillmentStatus
    ) {
        log.info("Search orders orgId={}, websiteId={}, from={}, to={}, status={}, financialStatus={}, fulfillmentStatus={}, page={}, size={}, sort={}",
                orgId, websiteId, from, to, status, financialStatus, fulfillmentStatus, page, size, sort);
        return orderService.searchOrders(orgId, websiteId, from, to, page, size, sort, status, financialStatus, fulfillmentStatus);
    }

    // search order by external order id/number
    @GetMapping("/search")
    public PagedOrderResponse searchByOrderIdAndName(
            @RequestParam UUID orgId,
            @RequestParam UUID websiteId,
            @RequestParam(required = false) String externalOrderId,
            @RequestParam(required = false) String externalOrderNumber,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "50") Integer size
    ) {
        return orderService.searchByOrderIdAndName(orgId, websiteId, externalOrderId, externalOrderNumber, page, size);
    }

    // get order details by id
    @GetMapping("/{orderId}")
    public OrderResponse getOrderById(@PathVariable UUID orderId) {
        return orderService.getOrderById(orderId);
    }

    // update order details (full replace)
    @PutMapping("/{orderId}")
    public OrderResponse updateOrder(
            @PathVariable UUID orderId,
            @RequestBody OrderUpdateRequest request
    ) {
        return orderService.updateOrder(orderId, request);
    }

    // update order details (partial replace)
    @PatchMapping("/{orderId}")
    public OrderResponse patchOrder(
            @PathVariable UUID orderId,
            @RequestBody OrderPatchRequest req
    ) {
        log.info("Request to patch orderId={} payload={}", orderId, req);
        return orderService.patchOrder(orderId, req);
    }

    // delete order
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

}
