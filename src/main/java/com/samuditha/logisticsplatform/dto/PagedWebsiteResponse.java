package com.samuditha.logisticsplatform.dto;
import lombok.*;

import java.util.List;
@Data
@Builder
public class PagedWebsiteResponse {
    private List<WebsiteResponse> data;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
}

