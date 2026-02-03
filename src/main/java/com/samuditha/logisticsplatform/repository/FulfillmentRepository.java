package com.samuditha.logisticsplatform.repository;

import com.samuditha.logisticsplatform.entity.Fulfillment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface FulfillmentRepository extends JpaRepository<Fulfillment, UUID> {

    boolean existsByOrder_OrderIdAndExternalFulfillmentIdIgnoreCase(UUID orderId, String externalFulfillmentId);

    @Query("""
        SELECT f
        FROM Fulfillment f
        WHERE f.order.orderId = :orderId
          AND (:from IS NULL OR f.updatedAt >= :from)
          AND (:to IS NULL OR f.updatedAt <= :to)
          AND (:status IS NULL OR f.fulfillmentStatus = :status)
          AND (
              :carrier IS NULL OR :carrier = '' OR
              lower(f.carrier) like lower(concat('%', :carrier, '%'))
          )
    """)
    Page<Fulfillment> searchFulfillments(
            @Param("orderId") UUID orderId,
            @Param("from") Instant from,
            @Param("to") Instant to,
            @Param("status") Fulfillment.FulfillmentStatus status,
            @Param("carrier") String carrier,
            Pageable pageable
    );

    @Query("""
        SELECT f
        FROM Fulfillment f
        WHERE f.order.orderId = :orderId
          AND (
                :externalFulfillmentId IS NULL OR :externalFulfillmentId = '' OR
                LOWER(f.externalFulfillmentId) LIKE LOWER(CONCAT('%', :externalFulfillmentId, '%'))
              )
    """)
    Page<Fulfillment> searchByExternalFulfillmentId(
            @Param("orderId") UUID orderId,
            @Param("externalFulfillmentId") String externalFulfillmentId,
            Pageable pageable
    );

    Optional<Fulfillment> findByFulfillmentIdAndOrder_OrderId(UUID fulfillmentId, UUID orderId);

    boolean existsByOrder_OrderIdAndExternalFulfillmentIdIgnoreCaseAndFulfillmentIdNot(UUID orderId, String externalFulfillmentId, UUID fulfillmentId);

}
