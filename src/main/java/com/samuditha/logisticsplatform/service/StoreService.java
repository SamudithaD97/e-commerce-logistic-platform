package com.samuditha.logisticsplatform.service;

import com.samuditha.logisticsplatform.dto.OrganizationResponse;
import com.samuditha.logisticsplatform.dto.PagedWebsiteResponse;
import com.samuditha.logisticsplatform.dto.WebsiteCreateRequest;
import com.samuditha.logisticsplatform.dto.WebsiteResponse;

import java.util.UUID;

public interface StoreService {

    WebsiteResponse addStore(WebsiteCreateRequest websiteCreateRequest, UUID orgId);

    PagedWebsiteResponse searchWebsites(UUID orgId, UUID websiteId, String code, String domain, Integer page, Integer size);

}
