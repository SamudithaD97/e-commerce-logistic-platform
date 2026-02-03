package com.samuditha.logisticsplatform.repository;

import com.samuditha.logisticsplatform.dto.TrackingCreateRequest;
import com.samuditha.logisticsplatform.dto.TrackingResponse;
import com.samuditha.logisticsplatform.entity.Tracking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.UUID;

public interface TrackingRepository extends JpaRepository <Tracking, UUID> {

    boolean existsByFulfillment_FulfillmentIdAndTrackingNumberIgnoreCase(UUID fulfillmentId, String trackingNumber);

    @Modifying
    @Query("""
        UPDATE Tracking t
           SET t.isPrimary = false
         WHERE t.fulfillment.fulfillmentId = :fulfillmentId
           AND t.isPrimary = true
    """)
    void clearPrimaryForFulfillment(UUID fulfillmentId);

    @Query("""
        SELECT t
        FROM Tracking t
        WHERE t.fulfillment.fulfillmentId = :fulfillmentId
          AND (:from IS NULL OR t.lastEventAt >= :from)
          AND (:to IS NULL OR t.lastEventAt <= :to)
          AND (:status IS NULL OR t.status = :status)
          AND (:carrier IS NULL OR :carrier = '' OR LOWER(t.carrier) LIKE LOWER(CONCAT('%', :carrier, '%')))
          AND (:trackingNumber IS NULL OR :trackingNumber = '' OR LOWER(t.trackingNumber) LIKE LOWER(CONCAT('%', :trackingNumber, '%')))
    """)
    Page<Tracking> searchTracking(UUID fulfillmentId, Instant from, Instant to, Tracking.Status status, String carrier, String trackingNumber, Pageable pageable);
}
