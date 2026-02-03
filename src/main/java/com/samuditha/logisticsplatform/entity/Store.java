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
@Table(name = "store")
public class Store {

    @Id
    @Column(name = "store_id", columnDefinition = "BINARY(16)")
    private UUID storeId;

    @ManyToOne
    @JoinColumn(name = "tenant_id", referencedColumnName = "tenant_id", nullable = false)
    @JsonIgnore
    private Tenant tenant;

    @Column(name = "store_code", nullable = false)
    private String storeCode;

    @Column(name = "domain", nullable = false)
    private String domain;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform")
    private Platform platform = Platform.OTHER;

    @OneToMany(mappedBy = "store")
    @JsonIgnore
    private Collection<Order> orders;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Store.Status status = Store.Status.ACTIVE;

    @Column(name = "timezone", length = 64)
    private String timezone = "UTC";

    @Column(name = "currency", length = 3)
    private String currency = "USD";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        if (storeId == null) storeId = UUID.randomUUID();
        if (createdAt == null) createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    public enum Platform { SHOPIFY, NETSUITE, CUSTOM, MAGENTO, OTHER }

    public enum Status { ACTIVE, INACTIVE }

}
