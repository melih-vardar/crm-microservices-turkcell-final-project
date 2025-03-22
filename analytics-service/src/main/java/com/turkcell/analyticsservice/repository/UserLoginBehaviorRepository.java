package com.turkcell.analyticsservice.repository;

import com.turkcell.analyticsservice.model.UserLoginBehavior;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserLoginBehaviorRepository extends JpaRepository<UserLoginBehavior, UUID> {
}
