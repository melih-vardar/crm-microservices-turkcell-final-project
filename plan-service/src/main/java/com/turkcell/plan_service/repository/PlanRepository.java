package com.turkcell.plan_service.repository;

import com.turkcell.plan_service.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PlanRepository extends JpaRepository<Plan, UUID> {

    @Query("SELECT p FROM plans p JOIN p.basicPlan bp WHERE bp.name = :name")
    Plan findByBasicPlanName(@Param("name") String name);

    @Query("SELECT p FROM plans p JOIN p.basicPlan bp WHERE bp.name = :name AND p.durationInMonths = :durationInMonths")
    Plan findByBasicPlanNameAndDurationInMonths(@Param("name") String name,
            @Param("durationInMonths") int durationInMonths);

}
