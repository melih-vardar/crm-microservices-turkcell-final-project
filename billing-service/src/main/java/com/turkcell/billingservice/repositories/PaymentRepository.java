package com.turkcell.billingservice.repositories;

import com.turkcell.billingservice.entities.Payment;
import com.turkcell.billingservice.entities.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    // 1. Tek Ödeme Varsa (OneToOne ilişki)
    Optional<Payment> findByBillId(UUID billId); // Spring Data JPA kurallarına uygun isimlendir
    List<Payment> findBySuccess(Boolean success);

    List<Payment> findByPaymentMethod(PaymentMethod method);
}