package com.samuditha.logisticsplatform.service;

import com.samuditha.logisticsplatform.dto.PagedTrackingResponse;
import com.samuditha.logisticsplatform.dto.TrackingCreateRequest;
import com.samuditha.logisticsplatform.dto.TrackingResponse;
import com.samuditha.logisticsplatform.entity.Tracking;

import java.time.Instant;
import java.util.UUID;

public interface TrackingService {

    TrackingResponse createTracking(UUID fulfillmentId, TrackingCreateRequest req);

    PagedTrackingResponse searchTracking(UUID fulfillmentId, Instant from, Instant to, Integer page, Integer size, String sort, Tracking.Status status,
                                         String carrier, String trackingNumber);
}
