package com.turkcell.billingservice.repositories;

import com.turkcell.billingservice.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}