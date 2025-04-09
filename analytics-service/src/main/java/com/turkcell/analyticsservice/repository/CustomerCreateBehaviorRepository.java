package com.turkcell.analyticsservice.repository;

import com.turkcell.analyticsservice.model.CustomerCreateBehavior;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerCreateBehaviorRepository extends JpaRepository<CustomerCreateBehavior, UUID> {

    CustomerCreateBehavior findByCustomerId(UUID customerId);
}
