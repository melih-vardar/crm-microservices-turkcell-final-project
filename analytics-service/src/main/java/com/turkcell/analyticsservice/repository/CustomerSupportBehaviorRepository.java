package com.turkcell.analyticsservice.repository;

import com.turkcell.analyticsservice.model.CustomerSupportBehavior;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerSupportBehaviorRepository extends JpaRepository<CustomerSupportBehavior, UUID> {
}
