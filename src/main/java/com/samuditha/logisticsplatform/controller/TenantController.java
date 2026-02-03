package com.samuditha.logisticsplatform.controller;

import com.samuditha.logisticsplatform.dto.*;
import com.samuditha.logisticsplatform.service.TenantService;
import com.samuditha.logisticsplatform.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/organizations")
public class TenantController {
    private final TenantService tenantService;

    private final StoreService storeService;
    private static final Logger log = LoggerFactory.getLogger(TenantController.class);

    // add organization
    @PostMapping(value = "")
    public OrganizationResponse addOrganization(@RequestBody OrganizationCreateRequest organizationCreateRequest) {
        log.info("Request to add organization {}", organizationCreateRequest);
        return tenantService.addOrganization(organizationCreateRequest);
    }

    // get all organizations
    @GetMapping(value = "")
    public PagedOrganizationResponse listOrganizations(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String name
    ) {
        log.info("Request to get details of organization {}", name);
        return tenantService.listOrganizations(page, size, status, name);
    }

    // get organization by id
    @GetMapping("/{id}")
    public OrganizationResponse getOrganizationById(@PathVariable UUID id) {
        log.info("Request to get details of organization {}", id);
        return tenantService.getOrganizationById(id);
    }
}
