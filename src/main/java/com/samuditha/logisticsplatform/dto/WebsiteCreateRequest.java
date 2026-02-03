package com.samuditha.logisticsplatform.dto;

import com.samuditha.logisticsplatform.entity.Store;
import com.samuditha.logisticsplatform.entity.Tenant;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebsiteCreateRequest {

    @NotBlank(message = "Website code is required")
    @Size(min = 2, max = 100)
    private String code;

    @NotBlank(message = "Website name is required")
    @Size(min = 2, max = 255)
    private String name;

    @NotNull(message = "Platform is required")
    private Store.Platform platform;

    private String domain;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Store.Status status = Store.Status.ACTIVE;


}
