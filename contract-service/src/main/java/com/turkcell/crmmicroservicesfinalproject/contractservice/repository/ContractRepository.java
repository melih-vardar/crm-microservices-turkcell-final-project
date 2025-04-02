package com.turkcell.crmmicroservicesfinalproject.contractservice.repository;

import com.turkcell.crmmicroservicesfinalproject.contractservice.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContractRepository extends JpaRepository<Contract, UUID> {
    List<Contract> findByCustomerId(UUID customerId);

    List<Contract> findByIsActiveTrue();
}