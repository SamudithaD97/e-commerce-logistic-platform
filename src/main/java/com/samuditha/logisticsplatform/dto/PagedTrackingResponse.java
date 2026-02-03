package com.samuditha.logisticsplatform.dto;

import com.samuditha.logisticsplatform.entity.Tracking;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class PagedTrackingResponse {

    private List<TrackingResponse> data;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;

    public static PagedTrackingResponse from(Page<Tracking> pageResult) {
        return PagedTrackingResponse.builder()
                .data(pageResult.getContent()
                        .stream()
                        .map(TrackingResponse::from)
                        .collect(Collectors.toList()))
                .page(pageResult.getNumber())
                .size(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .hasNext(pageResult.hasNext())
                .build();
    }
}
