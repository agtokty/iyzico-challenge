package com.iyzico.challenge.exception;

/**
 * @author agtokty
 */
public class PaymentException extends Exception {
    public PaymentException(String payment_error, Exception e) {
        super(payment_error, e);
    }
}
