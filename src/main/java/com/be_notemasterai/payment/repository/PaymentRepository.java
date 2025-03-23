package com.be_notemasterai.payment.repository;

import com.be_notemasterai.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}