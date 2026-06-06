package com.galaxy.ordering.service;

import com.galaxy.ordering.dto.CartAddRequest;
import com.galaxy.ordering.entity.Cart;
import com.galaxy.ordering.entity.Product;
import com.galaxy.ordering.mapper.CartMapper;
import com.galaxy.ordering.service.cart.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartMapper cartMapper;

    @Test
    void addItem_createsNewEntry() {
        CartAddRequest req = new CartAddRequest();
        req.setMerchantId(1L);
        req.setProductId(1L);
        req.setQuantity(2);
        Cart cart = cartService.addItem(2L, req);
        assertEquals(2L, cart.getUserId());
        assertEquals(2, cart.getQuantity());
    }

    @Test
    void addItem_accumulatesExisting() {
        CartAddRequest req1 = new CartAddRequest();
        req1.setMerchantId(1L);
        req1.setProductId(1L);
        req1.setQuantity(1);
        cartService.addItem(2L, req1);

        CartAddRequest req2 = new CartAddRequest();
        req2.setMerchantId(1L);
        req2.setProductId(1L);
        req2.setQuantity(3);
        Cart cart = cartService.addItem(2L, req2);
        assertEquals(4, cart.getQuantity());
    }

    @Test
    void remove_deletesEntry() {
        CartAddRequest req = new CartAddRequest();
        req.setMerchantId(1L);
        req.setProductId(1L);
        req.setQuantity(1);
        Cart cart = cartService.addItem(2L, req);
        Long id = cart.getId();

        cartService.remove(id);
        assertNull(cartMapper.selectById(id));
    }
}
