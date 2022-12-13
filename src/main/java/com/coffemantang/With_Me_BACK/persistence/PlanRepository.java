package com.coffemantang.With_Me_BACK.persistence;

import com.coffemantang.With_Me_BACK.dto.PlanDTO;
import com.coffemantang.With_Me_BACK.model.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    //
    @Modifying
    @Transactional
    @Query(value = " " +
            "UPDATE plan " +
            "SET state = " +
                "case " +
                    "when(:now < post_date and not state = 0) then 0 " +
                    "when(deadline < :now and :now < start_date and not state = 1) then 1 " +
                    "when(start_date < :now and :now < end_date and not state = 2) then 2 " +
                    "when(end_date < :now and not state = 3) then 3 " +
                    "else state " +
                "end " +
            "where plan_id = :planId", nativeQuery = true)
    void updateState(@Param("now") String now, @Param("planId") int planId);

    // 조회수 증가
    @Modifying
    @Transactional
    @Query(value = "UPDATE plan SET hit = hit + 1 WHERE plan_id = :planId", nativeQuery = true)
    void updateHit(@Param("planId") int planId);

    Plan findById(int planId);

    // planId에 맞는 여행이 state가 3이면 카운트
    int countByPlanIdAndState(int planId, int state);

    // 지역으로 검색하기(최신순)
    Page<Plan> findAllByPlaceLikeOrderByPostDateDesc(String place, Pageable pageable);

    // PlanId와 memberId 에 맞는 로우가 있으면 카운트
    int countByPlanIdAndMemberId(int planId, int memberId);

    // 나의 여행 리스트 가져오기 (최신순)
    Page<Plan> findByMemberIdOrderByPostDateDesc(int memberId, Pageable pageable);
}
