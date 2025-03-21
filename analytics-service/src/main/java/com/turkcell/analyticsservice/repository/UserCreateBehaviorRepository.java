package com.turkcell.analyticsservice.repository;

import com.turkcell.analyticsservice.model.UserCreateBehavior;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserCreateBehaviorRepository extends JpaRepository<UserCreateBehavior, UUID> {
}
