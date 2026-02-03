package com.samuditha.logisticsplatform.controller;

import com.samuditha.logisticsplatform.dto.PagedTrackingResponse;
import com.samuditha.logisticsplatform.dto.TrackingCreateRequest;
import com.samuditha.logisticsplatform.dto.TrackingResponse;
import com.samuditha.logisticsplatform.entity.Tracking;
import com.samuditha.logisticsplatform.service.TrackingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/fulfillments/{fulfillmentId}")
public class TrackingController {
    private static final Logger log = LoggerFactory.getLogger(TrackingController.class);

    private final TrackingService trackingService;

    @PostMapping("/tracking")
    public TrackingResponse createTracking(@PathVariable UUID fulfillmentId, @Valid @RequestBody TrackingCreateRequest request)
    {
        log.info("Create tracking for fulfillmentId={}, trackingNumber={}", fulfillmentId, request.getTrackingNumber());
        return trackingService.createTracking(fulfillmentId, request);
    }

    @GetMapping("/tracking")
    public PagedTrackingResponse searchTracking(
            @PathVariable UUID fulfillmentId,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "50") Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Tracking.Status status,
            @RequestParam(required = false) String carrier,
            @RequestParam(required = false) String trackingNumber
    ) {
        return trackingService.searchTracking(fulfillmentId, from, to, page, size, sort, status, carrier, trackingNumber);
    }

}
