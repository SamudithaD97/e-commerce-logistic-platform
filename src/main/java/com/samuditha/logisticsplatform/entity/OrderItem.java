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
@Table(name = "order_items")
public class OrderItem {

    @Id
    @Column(name = "order_item_id", columnDefinition = "BINARY(16)")
    private UUID orderItemId;

    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    @JsonIgnore
    private Tenant tenant;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @Column(name = "sku")
    private String sku;

    @Column(name = "quantity_ordered")
    private Integer quantityOrdered;

    @PrePersist
    public void prePersist() {
        if (orderItemId == null) orderItemId = UUID.randomUUID();
    }
}
