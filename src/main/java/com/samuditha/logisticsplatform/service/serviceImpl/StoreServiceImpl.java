package com.samuditha.logisticsplatform.service.serviceImpl;

import com.samuditha.logisticsplatform.dto.OrganizationResponse;
import com.samuditha.logisticsplatform.dto.PagedWebsiteResponse;
import com.samuditha.logisticsplatform.dto.WebsiteCreateRequest;
import com.samuditha.logisticsplatform.dto.WebsiteResponse;
import com.samuditha.logisticsplatform.entity.Store;
import com.samuditha.logisticsplatform.entity.Tenant;
import com.samuditha.logisticsplatform.repository.StoreRepository;
import com.samuditha.logisticsplatform.repository.TenantRepository;
import com.samuditha.logisticsplatform.service.StoreService;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final TenantRepository tenantRepository;

    @Override
    public WebsiteResponse addStore(WebsiteCreateRequest websiteCreateRequest, UUID orgId) {
        try {

            Tenant tenant = tenantRepository.findByTenantId(orgId);
            if (tenant == null) {
                throw  new IllegalArgumentException("Organization not found: " + orgId);
            }

            Optional<Store> existing = storeRepository.findByTenant_TenantIdAndStoreCodeIgnoreCase(orgId, websiteCreateRequest.getCode());
            if (existing.isPresent()) {
                throw new IllegalArgumentException("Store code already exists for this organization");
            }

            Store store = new Store();
            store.setTenant(tenant);
            store.setStoreCode(websiteCreateRequest.getCode());
            store.setStoreName(websiteCreateRequest.getName());
            store.setPlatform(websiteCreateRequest.getPlatform());
            store.setStatus(websiteCreateRequest.getStatus());
            store.setDomain(websiteCreateRequest.getDomain());

            Store savedStore = storeRepository.save(store);
            return WebsiteResponse.builder()
                    .id(savedStore.getStoreId())
                    .orgId(orgId)
                    .code(savedStore.getStoreCode())
                    .name(savedStore.getStoreName())
                    .platform(savedStore.getPlatform())
                    .domain(savedStore.getDomain())
                    .status(savedStore.getStatus())
                    .build();


        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while creating store", e);
        }
    }

    @Override
    public PagedWebsiteResponse searchWebsites(UUID orgId, UUID websiteId, String code, String domain, Integer page, Integer size){
        try{

            int pageNo = (page == null || page < 0) ? 0 : page;
            int pageSize = (size == null || size < 1) ? 50 : Math.min(size, 500);

            Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "updatedAt"));

            Page<Store> result;
            boolean hasCode = code != null && !code.isBlank();
            boolean hasDomain = domain != null && !domain.isBlank();

            if (websiteId != null) {
                result =  storeRepository.findWebsiteDetailsByWebsiteIdAndOrgId(websiteId, orgId,pageable);

            }
            else if(hasCode && hasDomain) {
                result = storeRepository.findWebsiteDetailsByCodeAndDomain(orgId, code, domain, pageable);
            } else if (hasCode) {
                result = storeRepository.findWebsiteDetailsByCode(orgId, code, pageable);
            } else if (hasDomain) {
                result = storeRepository.findWebsiteDetailsByDomain(orgId, domain, pageable);
            } else {
                // no filters
                result = storeRepository.findWebsiteDetailsByOrgId(orgId, pageable);
            }
            List<WebsiteResponse> data = result.getContent().stream()
                    .map(this::toResponse)
                    .toList();

            return PagedWebsiteResponse.builder()
                    .data(data)
                    .page(result.getNumber())
                    .size(result.getSize())
                    .totalElements(result.getTotalElements())
                    .totalPages(result.getTotalPages())
                    .hasNext(result.hasNext())
                    .build();

        }catch (DataAccessException e) {
            throw new RuntimeException("Database error while searching websites", e);
        }
    }

    private WebsiteResponse toResponse(Store s) {
        return WebsiteResponse.builder()
                .id(s.getStoreId())
                .orgId(s.getTenant().getTenantId())
                .code(s.getStoreCode())
                .name(s.getStoreName())
                .platform(s.getPlatform())
                .domain(s.getDomain())
                .status(s.getStatus())
                .build();
    }


}
