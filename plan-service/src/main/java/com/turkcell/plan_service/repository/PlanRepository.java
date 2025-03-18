package com.turkcell.plan_service.repository;

import com.turkcell.plan_service.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlanRepository extends JpaRepository<Plan, UUID> {

}
