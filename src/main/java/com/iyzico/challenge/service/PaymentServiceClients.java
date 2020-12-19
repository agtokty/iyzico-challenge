package com.iyzico.challenge.service;

import com.iyzico.challenge.exception.PaymentException;
import com.iyzico.challenge.service.payment.IyzicoPaymentService;
import com.iyzico.challenge.service.payment.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Service
public class PaymentServiceClients {

    private PaymentService iyzicoPaymentService;

    @Autowired
    public PaymentServiceClients(PaymentService iyzicoPaymentService) {
        this.iyzicoPaymentService = iyzicoPaymentService;
    }

    @Async
    public CompletableFuture<String> call(BigDecimal price) throws PaymentException {
        iyzicoPaymentService.pay(price);
        return CompletableFuture.completedFuture("success");
    }
}
