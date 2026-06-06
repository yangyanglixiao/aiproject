package com.galaxy.ordering.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.galaxy.ordering.common.Result;
import com.galaxy.ordering.dto.OrderCreateRequest;
import com.galaxy.ordering.entity.Order;
import com.galaxy.ordering.entity.OrderItem;
import com.galaxy.ordering.service.auth.AuthService;
import com.galaxy.ordering.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final AuthService authService;

    @PostMapping
    public Result<Order> create(@RequestBody OrderCreateRequest request, Authentication authentication) {
        String username = authentication.getName();
        Long userId = authService.getCurrentUser(username).getId();
        return Result.ok(orderService.create(userId, request));
    }

    @GetMapping
    public Result<Page<Order>> list(Authentication authentication,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        String username = authentication.getName();
        Long userId = authService.getCurrentUser(username).getId();
        return Result.ok(orderService.listByUser(userId, page, size));
    }

    @GetMapping("/{id}")
    public Result<Order> detail(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        Long userId = authService.getCurrentUser(username).getId();
        Order order = orderService.getById(id, userId);
        return Result.ok(order);
    }

    @GetMapping("/{id}/items")
    public Result<List<OrderItem>> items(@PathVariable Long id) {
        return Result.ok(orderService.getItemsByOrderId(id));
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        Long userId = authService.getCurrentUser(username).getId();
        orderService.cancel(id, userId);
        return Result.ok();
    }
}
