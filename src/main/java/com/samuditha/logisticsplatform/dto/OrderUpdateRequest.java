package com.samuditha.logisticsplatform.dto;

import java.util.UUID;

import com.samuditha.logisticsplatform.entity.Order;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
@Getter
@Setter
public class OrderUpdateRequest {

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
}
