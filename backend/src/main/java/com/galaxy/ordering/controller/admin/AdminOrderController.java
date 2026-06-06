package com.galaxy.ordering.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.galaxy.ordering.common.Result;
import com.galaxy.ordering.entity.Order;
import com.galaxy.ordering.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public Result<Page<Order>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        Page<Order> pageParam = new Page<>(page, size);
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Order> wrapper =
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        return Result.ok(orderService.page(pageParam, wrapper));
    }

    @PutMapping("/{id}/status")
    public Result<Order> updateStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        order.setStatus(request.getStatus());
        orderService.updateById(order);
        return Result.ok(order);
    }

    public static class StatusRequest {
        private String status;
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
