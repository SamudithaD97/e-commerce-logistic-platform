package com.samuditha.logisticsplatform.controller;

import com.samuditha.logisticsplatform.dto.*;
import com.samuditha.logisticsplatform.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/organizations/{orgId}")
public class StoreController {

    private final StoreService storeService;
    private static final Logger log = LoggerFactory.getLogger(TenantController.class);

    // add website under organization
    @PostMapping(value = "/websites")
    public WebsiteResponse addStore(@PathVariable UUID orgId, @RequestBody WebsiteCreateRequest websiteCreateRequest) {
        log.info("Request to create website {} to org Id {}", websiteCreateRequest, orgId);
        return storeService.addStore(websiteCreateRequest,orgId);
    }

    // search websites within an organization by websiteId/code/domain
    @GetMapping("/websites/search")
    public PagedWebsiteResponse searchWebsites(
            @PathVariable UUID orgId,
            @RequestParam(required = false) UUID websiteId,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String domain,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "50") Integer size
    ) {
        log.info("Search websites orgId={}, websiteId={}, code={}, domain={}", orgId, websiteId, code, domain);
        return storeService.searchWebsites(orgId, websiteId, code, domain, page, size);
    }


}
