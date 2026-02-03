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
@Table(name = "tracking_events")
public class TrackingEvent {

    @Id
    @Column(name = "tracking_event_id", columnDefinition = "BINARY(16)")
    private UUID trackingEventId;


    @ManyToOne
    @JoinColumn(name = "tracking_id", nullable = false)
    @JsonIgnore
    private Tracking tracking;

    @Column(name = "event_code")
    private String eventCode;

    @PrePersist
    public void prePersist() {
        if (trackingEventId == null) trackingEventId = UUID.randomUUID();
    }
}
