package com.samuditha.logisticsplatform.controller;

import com.samuditha.logisticsplatform.dto.*;

import com.samuditha.logisticsplatform.entity.Fulfillment;
import com.samuditha.logisticsplatform.service.FulfillmentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;


@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/orders/{orderId}/fulfillments")
public class FulfillmentController {

    private final FulfillmentService fulfillmentService;
    private static final Logger log = LoggerFactory.getLogger(FulfillmentController.class);

    // create new fulfillment
    @PostMapping("")
    public FulfillmentResponse createFulfillment(@PathVariable UUID orderId, @RequestBody FulfillmentCreateRequest request) {
        log.info("Request to create fulfillment for orderId={}, externalFulfillmentId={}", orderId, request.getExternalFulfillmentId());
        return fulfillmentService.createFulfillment(orderId, request);
    }

    // List/search fulfillments for an order (date range + pagination)
    @GetMapping("")
    public PagedFulfillmentResponse searchFulfillments(
            @PathVariable UUID orderId,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(required = false) Fulfillment.FulfillmentStatus status,
            @RequestParam(required = false) String carrier,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "50") Integer size,
            @RequestParam(defaultValue = "updatedAt,desc") String sort
    ) {
        return fulfillmentService.searchFulfillments(orderId, from, to, status, carrier, page, size, sort);
    }

    // get fulfillments by externalFulfillmentId
    @GetMapping("/search")
    public PagedFulfillmentResponse searchFulfillmentsByExternalFulfillmentId(
            @PathVariable UUID orderId,
            @RequestParam(required = false) String externalFulfillmentId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "50") Integer size
    ) {
        return fulfillmentService.searchFulfillmentsByExternalFulfillmentId(orderId, externalFulfillmentId, page, size);
    }

    // get fulfillment by Id
    @GetMapping("/{fulfillmentId}")
    public FulfillmentResponse getFulfillmentById(@PathVariable UUID orderId, @PathVariable UUID fulfillmentId) {
        return fulfillmentService.getFulfillmentById(orderId, fulfillmentId);
    }

    // update fulfillment (full replace)
    @PutMapping("/{fulfillmentId}")
    public FulfillmentResponse updateFulfillment(
            @PathVariable UUID orderId,
            @PathVariable UUID fulfillmentId,
            @RequestBody FulfillmentUpdateRequest request
    ) {
        return fulfillmentService.updateFulfillment(orderId, fulfillmentId, request);
    }

    // update fulfillment (partial replace)
    @PatchMapping("/{fulfillmentId}")
    public FulfillmentResponse patchFulfillment(
            @PathVariable UUID orderId,
            @PathVariable UUID fulfillmentId,
            @RequestBody FulfillmentPatchRequest request
    ) {
        return fulfillmentService.patchFulfillment(orderId, fulfillmentId, request);
    }

    // delete fulfillment
    @DeleteMapping("{fulfillmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFulfillment(
            @PathVariable UUID orderId,
            @PathVariable UUID fulfillmentId) {

        fulfillmentService.deleteFulfillment(orderId, fulfillmentId);
    }

}
