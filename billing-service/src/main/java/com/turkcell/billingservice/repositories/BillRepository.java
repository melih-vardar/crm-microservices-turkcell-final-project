package com.turkcell.billingservice.repositories;

import com.turkcell.billingservice.entities.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BillRepository extends JpaRepository<Bill, UUID> {
    List<Bill> findByPaidFalse();

    List<Bill> findByCustomerId(UUID customerId);
}