package com.coffemantang.With_Me_BACK.persistence;

import com.coffemantang.With_Me_BACK.model.ApplyPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplyPlanRepository extends JpaRepository<ApplyPlan, Integer> {

    ApplyPlan findByApplyPlanId(int applyPlanId);

    ApplyPlan findByMemberIdAndPlanIdAndState(int memberId, int planId, int state);
}
