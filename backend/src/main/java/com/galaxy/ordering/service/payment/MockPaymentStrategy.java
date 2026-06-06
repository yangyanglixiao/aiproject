package com.galaxy.ordering.service.payment;

import com.galaxy.ordering.common.BusinessException;
import com.galaxy.ordering.entity.Order;
import com.galaxy.ordering.entity.PaymentRecord;
import com.galaxy.ordering.mapper.OrderMapper;
import com.galaxy.ordering.mapper.PaymentRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MockPaymentStrategy implements PaymentStrategy {

    private final OrderMapper orderMapper;
    private final PaymentRecordMapper paymentRecordMapper;

    @Override
    public String getChannel() {
        return "mock";
    }

    @Override
    public PaymentResult pay(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!"UNPAID".equals(order.getPayStatus())) {
            throw new BusinessException("订单已支付");
        }

        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        String txnId = UUID.randomUUID().toString().substring(0, 8);

        order.setPayStatus("PAID");
        order.setPayMethod("mock");
        order.setStatus("PAID");
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);

        PaymentRecord record = new PaymentRecord();
        record.setOrderId(orderId);
        record.setOrderNo(order.getOrderNo());
        record.setAmount(order.getTotalAmount());
        record.setPayChannel("mock");
        record.setStatus("SUCCESS");
        record.setPayTime(LocalDateTime.now());
        paymentRecordMapper.insert(record);

        PaymentResult result = new PaymentResult();
        result.setSuccess(true);
        result.setMessage("支付成功");
        result.setTransactionId(txnId);
        return result;
    }
}
