package com.iyzico.challenge.repository.payment;

import com.iyzico.challenge.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
