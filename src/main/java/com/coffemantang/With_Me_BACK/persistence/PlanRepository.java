package com.coffemantang.With_Me_BACK.persistence;

import com.coffemantang.With_Me_BACK.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface PlanRepository extends JpaRepository<Plan, Integer> {

    // planId로 memberId 가져오기
    @Query(value = "SELECT member_id FROM plan WHERE plan_id = :planId", nativeQuery = true)
    int selectMemberIdById(@Param("planId") int planId);

    // 새로운 여행의 일정이 전에 참여한 일정과 겹친다면 카운트
    @Query(value = "SELECT COUNT(*) FROM plan p INNER JOIN plan_members pm ON p.plan_id = pm.plan_id " +
            "WHERE pm.member_id = :memberId AND p.state < :state AND " +
            "(p.start_date <= :endDate) AND (:startDate <= p.end_date)", nativeQuery = true)
    int selectCountByMemberIdAndStateAndDate(@Param("memberId") int memberId, @Param("state") int state,
                                             @Param("endDate") LocalDateTime endDate, @Param("startDate") LocalDateTime startDate);

    // planId와 memberId로 엔티티 가져오기
}
