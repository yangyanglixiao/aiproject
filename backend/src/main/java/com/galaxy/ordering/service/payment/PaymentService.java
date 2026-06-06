package com.galaxy.ordering.service.payment;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final List<PaymentStrategy> strategies;
    private Map<String, PaymentStrategy> strategyMap;

    public PaymentService(List<PaymentStrategy> strategies) {
        this.strategies = strategies;
    }

    @PostConstruct
    private void init() {
        strategyMap = strategies.stream()
                .collect(Collectors.toMap(PaymentStrategy::getChannel, Function.identity()));
    }

    public PaymentResult pay(String channel, Long orderId) {
        PaymentStrategy strategy = strategyMap.get(channel);
        if (strategy == null) {
            throw new RuntimeException("不支持的支付方式: " + channel);
        }
        return strategy.pay(orderId);
    }
}
