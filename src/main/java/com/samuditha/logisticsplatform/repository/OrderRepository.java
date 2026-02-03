package com.samuditha.logisticsplatform.repository;

import com.samuditha.logisticsplatform.entity.Order;
import com.samuditha.logisticsplatform.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByTenant_TenantIdAndStore_StoreIdAndExternalOrderId(
            UUID tenantId,
            UUID storeId,
            String externalOrderId
    );

    @Query("""
        SELECT o
        FROM Order o
        WHERE o.tenant.tenantId = :orgId
          AND (:websiteId IS NULL OR o.store.storeId = :websiteId)
          AND (:from IS NULL OR o.orderUpdatedAt >= :from)
          AND (:to IS NULL OR o.orderUpdatedAt <= :to)
          AND (:status IS NULL OR o.orderStatus = :status)
          AND (:financialStatus IS NULL OR o.financialStatus = :financialStatus)
          AND (:fulfillmentStatus IS NULL OR o.fulfillmentStatus = :fulfillmentStatus)
    """)
    Page<Order> searchOrders(UUID orgId, UUID websiteId, Instant from, Instant to, Order.OrderStatus status,
                             Order.FinancialStatus financialStatus, Order.FulfillmentStatus fulfillmentStatus, Pageable pageable);

    @Query("""
        SELECT o
        FROM Order o
        WHERE o.tenant.tenantId = :orgId
        AND o.store.storeId = :websiteId
        AND (:externalOrderId IS NULL OR o.externalOrderId = :externalOrderId)
        AND (:externalOrderNumber IS NULL OR o.externalOrderNumber = :externalOrderNumber)
    """)
    Page<Order> searchByOrderIdAndName(
            @Param("orgId") UUID orgId, @Param("websiteId") UUID websiteId, @Param("externalOrderId")String externalOrderId, @Param("externalOrderNumber")String externalOrderNumber, Pageable pageable);

    boolean existsByOrderIdAndTenant_TenantIdAndStore_StoreId(UUID orderId, UUID orgId, UUID websiteId);

    boolean existsByExternalOrderIdAndOrderIdNot(String externalOrderId, UUID orderId);
}
