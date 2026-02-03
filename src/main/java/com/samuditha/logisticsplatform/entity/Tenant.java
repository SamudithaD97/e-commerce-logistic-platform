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
@Table(name = "tenant")
public class Tenant {

    @Id
    @Column(name = "tenant_id", columnDefinition = "BINARY(16)")
    private UUID tenantId;

    @Column(name = "tenant_name", nullable = false, unique = true)
    private String tenantName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.ACTIVE;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "tenant")
    @JsonIgnore
    private Collection<Store> stores;

    @PrePersist
    public void prePersist() {
        if (tenantId == null) tenantId = UUID.randomUUID();
    }

    public enum Status { ACTIVE, INACTIVE }
}
