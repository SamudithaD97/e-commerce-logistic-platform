package com.samuditha.logisticsplatform.service;

import com.samuditha.logisticsplatform.dto.OrganizationCreateRequest;
import com.samuditha.logisticsplatform.dto.OrganizationResponse;
import com.samuditha.logisticsplatform.dto.PagedOrganizationResponse;

import java.util.UUID;


public interface TenantService {
    OrganizationResponse addOrganization(OrganizationCreateRequest organizationCreateRequest);

    PagedOrganizationResponse listOrganizations(Integer page, Integer size, String status, String name);

    OrganizationResponse getOrganizationById(UUID id);
}
