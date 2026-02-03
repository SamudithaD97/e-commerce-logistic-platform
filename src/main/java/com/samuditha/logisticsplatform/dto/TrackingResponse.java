package com.samuditha.logisticsplatform.dto;

import com.samuditha.logisticsplatform.entity.Tracking;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class TrackingResponse {
    private UUID trackingId;
    private UUID fulfillmentId;
    private String trackingNumber;
    private String carrier;
    private String trackingUrl;
    private Tracking.Status status;
    private boolean isPrimary;
    private Instant lastEventAt;

    public static TrackingResponse from(Tracking t) {
        return TrackingResponse.builder()
                .trackingId(t.getTrackingId())
                .fulfillmentId(t.getFulfillment().getFulfillmentId())
                .trackingNumber(t.getTrackingNumber())
                .carrier(t.getCarrier())
                .trackingUrl(t.getTrackingUrl())
                .status(t.getStatus())
                .isPrimary(t.isPrimary())
                .lastEventAt(t.getLastEventAt())
                .build();
    }
}
