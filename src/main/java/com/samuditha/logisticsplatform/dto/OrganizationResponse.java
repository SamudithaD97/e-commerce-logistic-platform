package com.samuditha.logisticsplatform.dto;

import com.samuditha.logisticsplatform.entity.Tenant;
import lombok.Builder;
import lombok.Getter;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class OrganizationResponse {

    private UUID id;
    private String name;
    private Tenant.Status status;
    private Instant createdAt;
    private Instant updatedAt;
}
