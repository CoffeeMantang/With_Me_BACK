package com.coffemantang.With_Me_BACK.persistence;

import com.coffemantang.With_Me_BACK.model.PlanDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanDetailRepository extends JpaRepository<PlanDetail, Integer> {

    // planId로 planDetailList 가져오기
    List<PlanDetail> findByPlanIdOrderByDetailDate(int planId);

    PlanDetail findById(int planId);

    PlanDetail findByPlanDetailId(int planDetailId);
}
