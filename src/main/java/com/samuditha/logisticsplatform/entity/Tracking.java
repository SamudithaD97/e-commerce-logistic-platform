package com.samuditha.logisticsplatform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "tracking")
public class Tracking {

    @Id
    @Column(name = "tracking_id", columnDefinition = "BINARY(16)")
    private UUID trackingId;

    @ManyToOne
    @JoinColumn(name = "fulfillment_id", nullable = false)
    @JsonIgnore
    private Fulfillment fulfillment;

    @Column(name = "tracking_number", nullable = false, length = 128)
    private String trackingNumber;

    @Column(name = "carrier", nullable = false, length = 255)
    private String carrier;

    @Column(name = "tracking_url", length = 512)
    private String trackingUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private Status status = Status.UNKNOWN;

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary = false;

    @Column(name = "last_event_at")
    private Instant lastEventAt;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "tracking", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Collection<TrackingEvent> events;

    @PrePersist
    public void prePersist() {
        if (trackingId == null) trackingId = UUID.randomUUID();
    }

    public enum Status {
        LABEL_CREATED,
        IN_TRANSIT,
        OUT_FOR_DELIVERY,
        DELIVERED,
        EXCEPTION,
        UNKNOWN
    }
}
