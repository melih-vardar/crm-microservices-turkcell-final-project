package com.turkcell.analyticsservice.repository;

import com.turkcell.analyticsservice.model.UserBehavior;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserBehaviorRepository extends JpaRepository<UserBehavior, UUID> {
}
