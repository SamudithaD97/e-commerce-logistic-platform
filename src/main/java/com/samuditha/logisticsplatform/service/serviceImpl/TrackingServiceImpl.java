package com.samuditha.logisticsplatform.service.serviceImpl;

import com.samuditha.logisticsplatform.dto.PagedTrackingResponse;
import com.samuditha.logisticsplatform.dto.TrackingCreateRequest;
import com.samuditha.logisticsplatform.dto.TrackingResponse;
import com.samuditha.logisticsplatform.entity.Fulfillment;
import com.samuditha.logisticsplatform.entity.Tracking;
import com.samuditha.logisticsplatform.repository.FulfillmentRepository;
import com.samuditha.logisticsplatform.repository.TrackingRepository;
import com.samuditha.logisticsplatform.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class TrackingServiceImpl implements TrackingService {

    private final TrackingRepository trackingRepository;
    private final FulfillmentRepository fulfillmentRepository;


    @Override
    public TrackingResponse createTracking(UUID fulfillmentId, TrackingCreateRequest req) {
        try {

            if (fulfillmentId == null) {
                throw new IllegalArgumentException("fulfillmentId must not be null");
            }

            Fulfillment fulfillment = fulfillmentRepository.findById(fulfillmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Fulfillment not found: " + fulfillmentId));

            if (req.getTrackingNumber() == null || req.getTrackingNumber().isBlank()) {
                throw new IllegalArgumentException("trackingNumber is required");
            }
            if (req.getCarrier() == null || req.getCarrier().isBlank()) {
                throw new IllegalArgumentException("carrier is required");
            }

            // Prevent duplicates in same fulfillment
            if (trackingRepository.existsByFulfillment_FulfillmentIdAndTrackingNumberIgnoreCase(fulfillmentId, req.getTrackingNumber())) {
                throw new IllegalArgumentException("Tracking number already exists for this fulfillment");
            }

            // If set as primary, clear other primary ones (optional rule)
            if (req.getIsPrimary()) {
                trackingRepository.clearPrimaryForFulfillment(fulfillmentId);
            }

            Tracking tracking = new Tracking();
            tracking.setFulfillment(fulfillment);
            tracking.setTrackingNumber(req.getTrackingNumber());
            tracking.setCarrier(req.getCarrier());
            tracking.setTrackingUrl(req.getTrackingUrl());
            tracking.setStatus(req.getStatus());
            tracking.setPrimary(req.getIsPrimary());
            tracking.setLastEventAt(req.getLastEventAt());
            tracking.setCreatedAt(Instant.now());
            tracking.setUpdatedAt(Instant.now());

            Tracking saved = trackingRepository.save(tracking);
            return TrackingResponse.from(saved);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Unable to create tracking (possible duplicate or invalid data)");
        }
    }

    @Override
    public PagedTrackingResponse searchTracking(UUID fulfillmentId, Instant from, Instant to, Integer page, Integer size, String sort,
                                                Tracking.Status status, String carrier, String trackingNumber) {
        try {

            if (fulfillmentId == null) {
                throw new IllegalArgumentException("Fulfillment ID must not be null");
            }

            if (!fulfillmentRepository.existsById(fulfillmentId)) {
                throw new IllegalArgumentException("Fulfillment not found: " + fulfillmentId);
            }

            int pageNo = (page == null || page < 0) ? 0 : page;
            int pageSize = (size == null || size < 1) ? 50 : Math.min(size, 500);

            // Default sort
            Sort sortObj = Sort.by(Sort.Direction.DESC, "updatedAt");

            if (sort != null && !sort.isBlank()) {
                String[] parts = sort.split(",");
                String field = parts[0].trim();

                // Allow only safe sortable fields
                if (!field.equals("updatedAt") && !field.equals("lastEventAt")) {
                    field = "updatedAt";
                }

                Sort.Direction dir = (parts.length > 1 && "asc".equalsIgnoreCase(parts[1]))
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

                sortObj = Sort.by(dir, field);
            }

            Pageable pageable = PageRequest.of(pageNo, pageSize, sortObj);

            Page<Tracking> result = trackingRepository.searchTracking(
                    fulfillmentId,
                    from,
                    to,
                    status,
                    (carrier == null || carrier.isBlank()) ? null : carrier.trim(),
                    (trackingNumber == null || trackingNumber.isBlank()) ? null : trackingNumber.trim(),
                    pageable
            );

            return PagedTrackingResponse.from(result);

        } catch (IllegalArgumentException ex) {
            // business validation errors
            throw ex;
        } catch (Exception ex) {
            // unexpected server errors
            throw new RuntimeException("Failed to search tracking records", ex);
        }
    }




}
