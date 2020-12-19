package com.iyzico.challenge.service.payment;

import org.springframework.stereotype.Service;

@Service
public class BankService {

    /**
     * Bank Latency Simulation (avg: 5 seconds)
     */
    public BankPaymentResponse pay(BankPaymentRequest request) {
        try {
            Thread.sleep(5000);
            return new BankPaymentResponse("200");
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
