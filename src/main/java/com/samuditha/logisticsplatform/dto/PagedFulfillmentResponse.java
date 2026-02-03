package com.samuditha.logisticsplatform.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
public class PagedFulfillmentResponse {
    private List<FulfillmentResponse> data;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
}