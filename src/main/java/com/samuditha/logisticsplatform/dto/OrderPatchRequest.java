package com.samuditha.logisticsplatform.dto;

import com.samuditha.logisticsplatform.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class OrderPatchRequest {

    private String externalOrderId;
    private String externalOrderNumber;
    private String customerEmail;
    private String currency;
    private Order.OrderStatus status;
    private Order.FinancialStatus financialStatus;
    private Order.FulfillmentStatus fulfillmentStatus;
    private BigDecimal orderTotal;
    private Instant orderCreatedAt;
    private Instant orderUpdatedAt;
}
