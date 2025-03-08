package com.turkcell.billingservice.repositories;

import com.turkcell.billingservice.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    List<Invoice> findByCustomerId(UUID customerId);
    List<Invoice> findByCustomerIdAndIsPaid(UUID customerId, boolean isPaid);
}