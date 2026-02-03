package com.samuditha.logisticsplatform.service.serviceImpl;

import com.samuditha.logisticsplatform.dto.OrganizationCreateRequest;
import com.samuditha.logisticsplatform.dto.OrganizationResponse;
import com.samuditha.logisticsplatform.dto.PagedOrganizationResponse;
import com.samuditha.logisticsplatform.entity.Tenant;
import com.samuditha.logisticsplatform.repository.TenantRepository;
import com.samuditha.logisticsplatform.service.TenantService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;

    @Override
    public OrganizationResponse addOrganization(OrganizationCreateRequest organizationCreateRequest) {
        try {
            Tenant.Status status =
                    (organizationCreateRequest.getStatus() == null || organizationCreateRequest.getStatus().isBlank())
                            ? Tenant.Status.ACTIVE
                            : organizationCreateRequest.getStatus().trim().equalsIgnoreCase("INACTIVE")
                            ? Tenant.Status.INACTIVE
                            : Tenant.Status.ACTIVE;
            Optional<Tenant> existing = tenantRepository.findByTenantNameIgnoreCase(organizationCreateRequest.getName());
            if (existing.isPresent()) {
                throw new IllegalArgumentException("Organization name already exists");
            }

            Tenant tenant = new Tenant();
            tenant.setTenantName(organizationCreateRequest.getName().trim());
            tenant.setStatus(status);
            tenant.setCreatedAt(Instant.now());
            tenant.setUpdatedAt(Instant.now());
            Tenant saved = tenantRepository.save(tenant);

            return OrganizationResponse.builder()
                    .id(saved.getTenantId())
                    .name(saved.getTenantName())
                    .status(saved.getStatus())
                    .createdAt(saved.getCreatedAt())
                    .updatedAt(saved.getUpdatedAt())
                    .build();

        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while creating organization", e);
        }
    }

    @Override
    public PagedOrganizationResponse listOrganizations(Integer page, Integer size, String status, String name) {
        try {
            int pageNo = (page == null || page < 0) ? 0 : page;
            int pageSize = (size == null || size < 1) ? 50 : Math.min(size, 500);

            Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "updatedAt"));

            Page<Tenant> result;

            boolean hasStatus = status != null && !status.isBlank();
            boolean hasName = name != null && !name.isBlank();

            if (hasStatus && hasName) {
                Tenant.Status status1 = Tenant.Status.valueOf(status.trim().toUpperCase());
                result = tenantRepository.findByStatusAndTenantNameContainingIgnoreCase(status1, name.trim(), pageable);
            } else if (hasStatus) {
                Tenant.Status status1 = Tenant.Status.valueOf(status.trim().toUpperCase());
                result = tenantRepository.findByStatus(status1, pageable);
            } else if (hasName) {
                result = tenantRepository.findByTenantNameContainingIgnoreCase(name.trim(), pageable);
            } else {
                result = tenantRepository.findAll(pageable);
            }

            List<OrganizationResponse> data = result.getContent().stream()
                    .map(this::toResponse)
                    .toList();

            return PagedOrganizationResponse.builder()
                    .data(data)
                    .page(result.getNumber())
                    .size(result.getSize())
                    .totalElements(result.getTotalElements())
                    .totalPages(result.getTotalPages())
                    .hasNext(result.hasNext())
                    .build();
        } catch (DataAccessException e) {
            log.error("DB error listing organizations status={}, name={}", status, name, e);
            throw new RuntimeException("Database error while listing organizations", e);
        }
    }

    @Override
    public OrganizationResponse getOrganizationById(UUID id) {
        try {
            Tenant tenant = tenantRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Organization not found: " + id));
            return toResponse(tenant);
        } catch (DataAccessException e) {
            log.error("DB error retrieving organization id={}", id, e);
            throw new RuntimeException("Database error while retrieving organization", e);
        }
    }

    private OrganizationResponse toResponse(Tenant t) {
        return OrganizationResponse.builder()
                .id(t.getTenantId())
                .name(t.getTenantName())
                .status(Tenant.Status.valueOf(t.getStatus().name()))
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }


}
