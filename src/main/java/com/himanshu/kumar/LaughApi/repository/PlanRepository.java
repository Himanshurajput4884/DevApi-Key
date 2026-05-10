package com.himanshu.kumar.LaughApi.repository;

import com.himanshu.kumar.LaughApi.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlanRepository extends JpaRepository<Plan, UUID> {
}
