package com.samuditha.logisticsplatform.dto;

import com.samuditha.logisticsplatform.entity.Fulfillment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class FulfillmentCreateRequest {

    @NotBlank(message = "externalFulfillmentId is required")
    @Size(max = 100)
    private String externalFulfillmentId;

    @NotNull(message = "status is required")
    private Fulfillment.FulfillmentStatus status;

    @NotBlank(message = "carrier is required")
    @Size(max = 100)
    private String carrier;

    @NotBlank(message = "serviceLevel is required")
    @Size(max = 100)
    private String serviceLevel;

    private Instant shippedAt;
    private Instant deliveredAt;
}
