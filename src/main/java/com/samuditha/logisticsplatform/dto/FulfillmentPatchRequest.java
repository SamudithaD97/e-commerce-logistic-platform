package com.samuditha.logisticsplatform.dto;

import com.samuditha.logisticsplatform.entity.Fulfillment;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
public class FulfillmentPatchRequest {
    private String externalFulfillmentId;
    private Fulfillment.FulfillmentStatus status;
    private String carrier;
    private String serviceLevel;
    private Instant shippedAt;
    private Instant deliveredAt;
}
