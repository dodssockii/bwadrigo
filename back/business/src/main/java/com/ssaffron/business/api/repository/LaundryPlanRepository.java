package com.ssaffron.business.api.repository;

import com.ssaffron.business.api.entity.LaundryPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaundryPlanRepository extends JpaRepository<LaundryPlanEntity, Integer> {
}
