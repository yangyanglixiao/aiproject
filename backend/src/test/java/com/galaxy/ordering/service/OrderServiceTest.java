package com.galaxy.ordering.service;

import com.galaxy.ordering.common.BusinessException;
import com.galaxy.ordering.dto.OrderCreateRequest;
import com.galaxy.ordering.entity.Order;
import com.galaxy.ordering.entity.OrderItem;
import com.galaxy.ordering.mapper.CartMapper;
import com.galaxy.ordering.service.order.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartMapper cartMapper;

    @Test
    void create_generatesOrderNo() {
        OrderCreateRequest req = new OrderCreateRequest();
        Order order = orderService.create(1L, req);
        assertNotNull(order);
        assertNotNull(order.getOrderNo());
        assertTrue(order.getOrderNo().startsWith("ORD"));
    }

    @Test
    void create_failsWhenNotFound() {
        OrderCreateRequest req = new OrderCreateRequest();
        assertThrows(BusinessException.class, () -> orderService.create(999L, req));
    }

    @Test
    void cancel_changesStatus() {
        OrderCreateRequest req = new OrderCreateRequest();
        Order order = orderService.create(1L, req);
        orderService.cancel(order.getId(), 1L);
        assertEquals("CANCELLED", orderService.getById(order.getId(), 1L).getStatus());
    }
}
