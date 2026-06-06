package com.galaxy.ordering.service.payment;

public interface PaymentStrategy {
    String getChannel();
    PaymentResult pay(Long orderId);
}
