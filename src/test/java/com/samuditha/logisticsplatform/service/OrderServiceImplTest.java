package com.samuditha.logisticsplatform.service;

import com.samuditha.logisticsplatform.dto.OrderCreateRequest;
import com.samuditha.logisticsplatform.entity.Order;
import com.samuditha.logisticsplatform.entity.Store;
import com.samuditha.logisticsplatform.entity.Tenant;
import com.samuditha.logisticsplatform.repository.OrderRepository;
import com.samuditha.logisticsplatform.repository.StoreRepository;
import com.samuditha.logisticsplatform.repository.TenantRepository;
import com.samuditha.logisticsplatform.service.serviceImpl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private OrderRepository orderRepository;
    @Mock private TenantRepository tenantRepository;
    @Mock private StoreRepository storeRepository;

    @InjectMocks private OrderServiceImpl orderService;

    @Test
    void createOrder_shouldThrow_whenOrganizationNotFound() {
        UUID orgId = UUID.randomUUID();

        OrderCreateRequest req = new OrderCreateRequest();
        req.setOrgId(orgId);

        when(tenantRepository.findById(orgId)).thenReturn(Optional.empty());

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(req));

        assertTrue(ex.getMessage().contains("Organization not found"));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_shouldSave_whenValid() {
        UUID orgId = UUID.randomUUID();
        UUID websiteId = UUID.randomUUID();

        Tenant tenant = new Tenant();
        tenant.setTenantId(orgId);

        Store store = new Store();
        store.setStoreId(websiteId);
        store.setTenant(tenant);

        OrderCreateRequest req = new OrderCreateRequest();
        req.setOrgId(orgId);
        req.setWebsiteId(websiteId);
        req.setExternalOrderId("EXT-1");

        when(tenantRepository.findById(orgId)).thenReturn(Optional.of(tenant));
        when(storeRepository.findByStoreIdAndTenant_TenantId(websiteId, orgId)).thenReturn(Optional.of(store));
        when(orderRepository.findByTenant_TenantIdAndStore_StoreIdAndExternalOrderId(orgId, websiteId, "EXT-1"))
                .thenReturn(Optional.empty());

        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        var res = orderService.createOrder(req);

        assertNotNull(res);
        verify(orderRepository).save(any(Order.class));
    }
}
