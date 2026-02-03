package com.samuditha.logisticsplatform.dto;

import com.samuditha.logisticsplatform.entity.Order;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class OrderResponse {
    private UUID id;
    private UUID orgId;
    private UUID websiteId;
    private String externalOrderId;
    private String externalOrderNumber;
    private Order.OrderStatus status;
    private Order.FinancialStatus financialStatus;
    private Order.FulfillmentStatus fulfillmentStatus;
    private String customerEmail;
    private BigDecimal orderTotal;
    private String currency;
    private Instant orderCreatedAt;
    private Instant orderUpdatedAt;
    private Instant ingestedAt;
    private String message;
}
