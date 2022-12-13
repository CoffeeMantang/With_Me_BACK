package com.coffemantang.With_Me_BACK.persistence;

import com.coffemantang.With_Me_BACK.model.ApplyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplyPlanRepository extends JpaRepository<ApplyPlan, Integer> {

    ApplyPlan findByApplyPlanId(int applyPlanId);


    Long countByPlanIdAndMemberIdAndState(int planId, int memberId, int state);

    ApplyPlan findByMemberIdAndPlanIdAndState(int memberId, int planId, int state);

    // 해당 memberId가 받은 신청 리스트(id최신순)
    @Query(value = "SELECT ap.* FROM apply_plan ap INNER JOIN plan p ON ap.plan_id = p.plan_id WHERE p.member_id = :memberId AND ap.state = :state", nativeQuery = true)
    List<ApplyPlan> selectApplyListByMemberIdOrderByApplyPlanIdDesc(@Param("memberId") int memberId, @Param("state") int state);

    List<ApplyPlan> findByMemberIdAndStateLessThanOrderByApplyPlanIdDesc(int memberId, int state);
}
