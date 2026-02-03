package com.samuditha.logisticsplatform.service;

import com.samuditha.logisticsplatform.dto.*;
import com.samuditha.logisticsplatform.entity.Fulfillment;

import java.time.Instant;
import java.util.UUID;

public interface FulfillmentService {
    FulfillmentResponse createFulfillment(UUID orderId,FulfillmentCreateRequest request);
    PagedFulfillmentResponse searchFulfillments(UUID orderId, Instant from, Instant to, Fulfillment.FulfillmentStatus status,
                                                String carrier, Integer page, Integer size, String sort);
    PagedFulfillmentResponse searchFulfillmentsByExternalFulfillmentId(UUID orderId, String externalFulfillmentId, Integer page, Integer size);

    FulfillmentResponse getFulfillmentById(UUID orderId, UUID fulfillmentId);

    FulfillmentResponse updateFulfillment(UUID orderId, UUID fulfillmentId, FulfillmentUpdateRequest req);

    FulfillmentResponse patchFulfillment(UUID orderId, UUID fulfillmentId, FulfillmentPatchRequest req);

    void deleteFulfillment(UUID orderId, UUID fulfillmentId);
}
