package com.coffemantang.With_Me_BACK.persistence;

import com.coffemantang.With_Me_BACK.model.PlanDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanDetailRepository extends JpaRepository<PlanDetail, Integer> {
}
