package com.samuditha.logisticsplatform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samuditha.logisticsplatform.repository.StoreRepository;
import com.samuditha.logisticsplatform.repository.TenantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest

@ActiveProfiles("test")
class OrderControllerIT {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final TenantRepository tenantRepository;
    private final StoreRepository storeRepository;

    OrderControllerIT(MockMvc mockMvc,
                      ObjectMapper objectMapper,
                      TenantRepository tenantRepository,
                      StoreRepository storeRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.tenantRepository = tenantRepository;
        this.storeRepository = storeRepository;
    }

    @Test
    void createOrder_shouldReturn200_or201_whenValid() throws Exception {

        UUID orgId = UUID.randomUUID();
        UUID websiteId = UUID.randomUUID();

        String requestBody = """
            {
              "orgId": "%s",
              "websiteId": "%s",
              "customerName": "John Doe"
            }
            """.formatted(orgId, websiteId);

        mockMvc.perform(post("/orders")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().is2xxSuccessful());
    }
}
