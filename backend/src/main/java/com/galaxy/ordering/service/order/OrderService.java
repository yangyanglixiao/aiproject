package com.galaxy.ordering.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.galaxy.ordering.common.BusinessException;
import com.galaxy.ordering.dto.OrderCreateRequest;
import com.galaxy.ordering.entity.*;
import com.galaxy.ordering.mapper.CartMapper;
import com.galaxy.ordering.mapper.OrderItemMapper;
import com.galaxy.ordering.mapper.OrderMapper;
import com.galaxy.ordering.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class OrderService extends ServiceImpl<OrderMapper, Order> {

    private static final AtomicLong ORDER_SEQ = new AtomicLong(0);
    private final OrderItemMapper orderItemMapper;
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    public Page<Order> listByUser(Long userId, int page, int size) {
        Page<Order> pageParam = new Page<>(page, size);
        return this.page(pageParam, new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreateTime));
    }

    public Order getById(Long orderId, Long userId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权查看此订单");
        }
        return order;
    }

    public List<OrderItem> getItemsByOrderId(Long orderId) {
        return orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));
    }

    @Transactional
    public Order create(Long userId, OrderCreateRequest request) {
        List<Cart> carts = cartMapper.selectList(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId));
        if (carts.isEmpty()) {
            throw new BusinessException("购物车为空");
        }

        Map<Long, List<Cart>> merchantGroup = new LinkedHashMap<>();
        for (Cart cart : carts) {
            merchantGroup.computeIfAbsent(cart.getMerchantId(), k -> new ArrayList<>()).add(cart);
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> allItems = new ArrayList<>();

        for (Map.Entry<Long, List<Cart>> entry : merchantGroup.entrySet()) {
            Long merchantId = entry.getKey();
            for (Cart cart : entry.getValue()) {
                Product product = productMapper.selectById(cart.getProductId());
                if (product == null || !"ON_SHELF".equals(product.getStatus())) {
                    throw new BusinessException("商品 " + product.getName() + " 不可用");
                }
                BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
                totalAmount = totalAmount.add(subtotal);

                OrderItem item = new OrderItem();
                item.setOrderId(0L);
                item.setMerchantId(merchantId);
                item.setProductId(cart.getProductId());
                item.setProductName(product.getName());
                item.setProductImage(product.getImage());
                item.setPrice(product.getPrice());
                item.setQuantity(cart.getQuantity());
                item.setSubtotal(subtotal);
                allItems.add(item);
            }
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");
        order.setPayStatus("UNPAID");
        this.save(order);

        for (OrderItem item : allItems) {
            item.setOrderId(order.getId());
        }
        orderItemMapper.insert(allItems);

        for (Long merchantId : merchantGroup.keySet()) {
            cartMapper.delete(new LambdaQueryWrapper<Cart>()
                    .eq(Cart::getUserId, userId)
                    .eq(Cart::getMerchantId, merchantId));
        }

        return order;
    }

    public void cancel(Long orderId, Long userId) {
        Order order = this.getById(orderId, userId);
        if (!"PENDING".equals(order.getStatus()) && !"PAID".equals(order.getStatus())) {
            throw new BusinessException("只有待支付或已支付状态的订单可以取消");
        }
        order.setStatus("CANCELLED");
        this.updateById(order);
    }

    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + String.format("%04d", ORDER_SEQ.incrementAndGet() % 10000);
    }
}
