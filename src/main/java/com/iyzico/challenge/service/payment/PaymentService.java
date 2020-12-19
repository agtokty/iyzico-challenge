package com.iyzico.challenge.service.payment;

import com.iyzico.challenge.exception.PaymentException;

import java.math.BigDecimal;

/**
 * @author agtokty
 */
public interface PaymentService {

    void pay(BigDecimal price) throws PaymentException;
}
