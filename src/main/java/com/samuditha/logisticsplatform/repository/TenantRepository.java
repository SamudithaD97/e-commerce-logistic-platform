package com.samuditha.logisticsplatform.repository;

import com.samuditha.logisticsplatform.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    Optional<Tenant> findByTenantNameIgnoreCase(String tenantName);

    Tenant findByTenantId(UUID tenantId);

    boolean existsByTenantNameIgnoreCase(String tenantName);


    Optional<Tenant> findByTenantName(String tenantName);

    Page<Tenant> findByStatus(Tenant.Status status, Pageable pageable);

    Page<Tenant> findByTenantNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Tenant> findByStatusAndTenantNameContainingIgnoreCase(Tenant.Status status, String name, Pageable pageable);
}
