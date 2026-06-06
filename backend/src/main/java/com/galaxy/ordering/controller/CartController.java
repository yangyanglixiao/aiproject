package com.galaxy.ordering.controller;

import com.galaxy.ordering.common.Result;
import com.galaxy.ordering.dto.CartAddRequest;
import com.galaxy.ordering.dto.CartUpdateRequest;
import com.galaxy.ordering.entity.Cart;
import com.galaxy.ordering.service.auth.AuthService;
import com.galaxy.ordering.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final AuthService authService;

    @GetMapping
    public Result<List<Cart>> list(Authentication authentication) {
        String username = authentication.getName();
        Long userId = authService.getCurrentUser(username).getId();
        return Result.ok(cartService.listByUser(userId));
    }

    @PostMapping("/add")
    public Result<Cart> add(@RequestBody CartAddRequest request, Authentication authentication) {
        String username = authentication.getName();
        Long userId = authService.getCurrentUser(username).getId();
        return Result.ok(cartService.addItem(userId, request));
    }

    @PutMapping("/item/{id}")
    public Result<Cart> update(@PathVariable Long id, @RequestBody CartUpdateRequest request, Authentication authentication) {
        String username = authentication.getName();
        Long userId = authService.getCurrentUser(username).getId();
        Cart cart = cartService.getById(id);
        if (cart == null || !cart.getUserId().equals(userId)) {
            return Result.fail(403, "无权操作");
        }
        return Result.ok(cartService.update(id, request.getQuantity()));
    }

    @DeleteMapping("/item/{id}")
    public Result<Void> remove(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        Long userId = authService.getCurrentUser(username).getId();
        Cart cart = cartService.getById(id);
        if (cart == null || !cart.getUserId().equals(userId)) {
            return Result.fail(403, "无权操作");
        }
        cartService.remove(id);
        return Result.ok();
    }
}
