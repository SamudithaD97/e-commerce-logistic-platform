package com.samuditha.logisticsplatform.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;
import java.util.Collection;
import java.util.UUID;
import java.time.Instant;
@Entity
@Getter
@Setter
@Table(name = "fulfillments")
public class Fulfillment {

    @Id
    @Column(name = "fulfillment_id", columnDefinition = "BINARY(16)")
    private UUID fulfillmentId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @Column(name = "external_fulfillment_id", nullable = false)
    private String externalFulfillmentId;

    @Column(name = "carrier", nullable = false)
    private String carrier;

    @Column(name = "serviceLevel", nullable = false)
    private String serviceLevel;

    @Column(name = "shipped_at")
    private Instant shippedAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "fulfillment_status", nullable = false)
    private Fulfillment.FulfillmentStatus fulfillmentStatus = Fulfillment.FulfillmentStatus.UNKNOWN;

    @OneToMany(mappedBy = "fulfillment")
    @JsonIgnore
    private Collection<Tracking> trackingList;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        if (fulfillmentId == null) fulfillmentId = UUID.randomUUID();
    }

    public enum FulfillmentStatus {
        UNKNOWN, UNFULFILLED, PARTIALLY_FULFILLED, FULFILLED, SHIPPED, DELIVERED, RETURNED, FAILED
    }
}
