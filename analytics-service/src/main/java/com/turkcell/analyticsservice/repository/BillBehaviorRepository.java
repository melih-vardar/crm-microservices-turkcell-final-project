package com.turkcell.analyticsservice.repository;

import com.turkcell.analyticsservice.model.BillBehavior;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BillBehaviorRepository extends JpaRepository<BillBehavior, UUID> {
}
