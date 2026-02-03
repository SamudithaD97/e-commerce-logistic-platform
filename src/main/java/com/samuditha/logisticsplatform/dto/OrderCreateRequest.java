package com.samuditha.logisticsplatform.dto;

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
public class OrderCreateRequest {

    @NotNull
    private UUID orgId;

    @NotNull
    private UUID websiteId;

    @NotBlank
    private String externalOrderId;

    private String externalOrderNumber;

    @NotNull
    private Order.OrderStatus status;

    @NotNull
    private Order.FinancialStatus financialStatus;

    @NotNull
    private Order.FulfillmentStatus fulfillmentStatus;

    @Email
    private String customerEmail;

    // request uses: orderTotal
    @NotNull
    private BigDecimal orderTotal;

    private String currency;

    private Instant orderCreatedAt;
    private Instant orderUpdatedAt;
}
