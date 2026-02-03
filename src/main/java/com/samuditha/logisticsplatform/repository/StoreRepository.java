package com.samuditha.logisticsplatform.repository;

import com.samuditha.logisticsplatform.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {
    Optional<Store> findByTenant_TenantIdAndStoreCodeIgnoreCase(UUID tenantId, String storeCode);

    @Query("SELECT u FROM Store u  WHERE " +
            "u.tenant.tenantId IN :orgId  AND u.storeCode IN :code AND u.domain IN :domain")
    Page<Store> findWebsiteDetailsByCodeAndDomain(UUID orgId, String code, String domain, Pageable pageable);

    @Query("SELECT u FROM Store u  WHERE " +
            "u.tenant.tenantId IN :orgId  AND u.storeCode IN :code")
    Page<Store> findWebsiteDetailsByCode(UUID orgId, String code, Pageable pageable);

    @Query("SELECT u FROM Store u  WHERE " +
            "u.tenant.tenantId IN :orgId AND u.domain IN :domain")
    Page<Store> findWebsiteDetailsByDomain(UUID orgId, String domain, Pageable pageable);

    @Query("SELECT u FROM Store u  WHERE " +
            "u.tenant.tenantId IN :orgId ")
    Page<Store> findWebsiteDetailsByOrgId(UUID orgId, Pageable pageable);

    @Query("SELECT u FROM Store u  WHERE " +
            "u.tenant.tenantId IN :orgId AND u.storeId IN :websiteId")
    Page<Store> findWebsiteDetailsByWebsiteIdAndOrgId(UUID websiteId, UUID orgId,Pageable pageable);

    @Query("SELECT u FROM Store u  WHERE " +
            "u.tenant.tenantId IN :orgId AND u.storeId IN :websiteId")
    Store findOneWebsiteDetailsByWebsiteIdAndOrgId(UUID websiteId, UUID orgId);

    boolean existsByStoreIdAndTenant_TenantId(UUID storeId, UUID tenantId);

    Optional<Store> findByStoreIdAndTenant_TenantId(UUID websiteId, UUID orgId);

    Store findByStoreId(UUID storeId);

}
