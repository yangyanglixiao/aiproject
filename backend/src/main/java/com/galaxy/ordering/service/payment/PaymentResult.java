package com.galaxy.ordering.service.payment;

import lombok.Data;

@Data
public class PaymentResult {
    private boolean success;
    private String message;
    private String transactionId;
}
