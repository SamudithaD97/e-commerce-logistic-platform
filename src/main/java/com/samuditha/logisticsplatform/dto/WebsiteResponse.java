package com.samuditha.logisticsplatform.dto;

import com.samuditha.logisticsplatform.entity.Store;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@lombok.Builder
public class WebsiteResponse {
    private UUID id;
    private UUID orgId;
    private String code;
    private String name;
    private Store.Platform platform;
    private String domain;
    private Store.Status status;
}
