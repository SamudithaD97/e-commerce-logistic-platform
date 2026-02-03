package com.samuditha.logisticsplatform.dto;

import com.samuditha.logisticsplatform.entity.Fulfillment;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class FulfillmentResponse {
    private UUID id;
    private UUID orderId;
    private String externalFulfillmentId;
    private Fulfillment.FulfillmentStatus status;
    private String carrier;
    private String serviceLevel;
    private Instant shippedAt;
    private Instant deliveredAt;
    private Instant createdAt;
    private Instant updatedAt;

    public static FulfillmentResponse from(Fulfillment f) {
        return FulfillmentResponse.builder()
                .id(f.getFulfillmentId())
                .orderId(f.getOrder() != null ? f.getOrder().getOrderId() : null)
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
}
