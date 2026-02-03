package com.samuditha.logisticsplatform.dto;

import com.samuditha.logisticsplatform.entity.Tracking;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class TrackingCreateRequest {

    @NotBlank(message = "trackingNumber is required")
    private String trackingNumber;

    @NotBlank(message = "carrier is required")
    private String carrier;
    private String trackingUrl;
    private Tracking.Status status;
    private Boolean isPrimary;
    private Instant lastEventAt;
}
