package com.samuditha.logisticsplatform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order {

    @Id
    @Column(name = "order_id", columnDefinition = "BINARY(16)")
    private UUID orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    @JsonIgnore
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    @JsonIgnore
    private Store store;

    @Column(name = "external_order_id", nullable = false, length = 255)
    private String externalOrderId;

    @Column(name = "external_order_number", length = 128)
    private String externalOrderNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus = OrderStatus.CREATED;

    @Enumerated(EnumType.STRING)
    @Column(name = "financial_status", nullable = false)
    private FinancialStatus financialStatus = FinancialStatus.UNKNOWN;

    @Enumerated(EnumType.STRING)
    @Column(name = "fulfillment_status", nullable = false)
    private FulfillmentStatus fulfillmentStatus = FulfillmentStatus.UNKNOWN;

    @Column(name = "customer_email", length = 255)
    private String customerEmail;

    @Column(name = "order_total_amount", precision = 12, scale = 2)
    private BigDecimal orderTotalAmount = BigDecimal.ZERO;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "order_created_at")
    private Instant orderCreatedAt;

    @Column(name = "order_updated_at")
    private Instant orderUpdatedAt;

    @Column(name = "ingested_at", nullable = false, updatable = false)
    private Instant ingestedAt;

    @Column(name = "raw_payload_json", columnDefinition = "json")
    private String rawPayloadJson;

    @PrePersist
    public void prePersist() {
        if (orderId == null) orderId = UUID.randomUUID();
        if (ingestedAt == null) ingestedAt = Instant.now();
    }

    // ===== ENUMS =====

    public enum OrderStatus {
        CREATED, CONFIRMED, SHIPPED, DELIVERED, CANCELLED, RETURNED, CLOSED
    }

    public enum FinancialStatus {
        UNKNOWN, PENDING, AUTHORIZED, PAID, PARTIALLY_PAID, REFUNDED, PARTIALLY_REFUNDED, VOIDED
    }

    public enum FulfillmentStatus {
        UNKNOWN, UNFULFILLED, PARTIALLY_FULFILLED, FULFILLED, SHIPPED, DELIVERED, RETURNED
    }
}
