package com.turkcell.plan_service.repository;

import com.turkcell.plan_service.entity.BasicPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BasicPlanRepository extends JpaRepository<BasicPlan, UUID> {
}