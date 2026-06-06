package com.galaxy.ordering.controller;

import com.galaxy.ordering.common.Result;
import com.galaxy.ordering.service.payment.PaymentResult;
import com.galaxy.ordering.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pay")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/mock/{orderId}")
    public Result<PaymentResult> mockPay(@PathVariable Long orderId) {
        return Result.ok(paymentService.pay("mock", orderId));
    }
}
