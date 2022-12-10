package com.coffemantang.With_Me_BACK.persistence;

import com.coffemantang.With_Me_BACK.model.PlanMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanMembersRepository extends JpaRepository<PlanMembers, Integer> {

    // planId로 PlanMembers 엔티티 가져오기
    List<PlanMembers> findByPlanId(int planId);

    // planMembersId로 엔티티 가져오기
    PlanMembers findById(int planMembersId);

    // 작성하지 않은 평가 카운트
    @Query(value = "SELECT COUNT(*) FROM plan p INNER JOIN plan_members pm ON p.plan_id = pm.plan_id WHERE pm.member_id = :memberId AND pm.check = :check", nativeQuery = true)
    int selectCountByMemberIdAndCheck(@Param("memberId") int memberId, @Param("check") int check);

    // 여행 횟수 가져오기
    @Query(value = "SELECT COUNT(*) FROM plan_members WHERE member_id = :memberId AND `check` > :check", nativeQuery = true)
    int selectCountByMemberIdAndCheckGreaterThan(@Param("memberId") int memberId, @Param("check") int check);

    // planId와 memberId로 구성원에 있는지 확인
    long countByPlanIdAndMemberId(int planId, int memberId);

    // planId와 memberId로 check 가져오기
    @Query(value = "SELECT check FROM plan_members WHERE plan_id = :planId AND member_id = :memberId", nativeQuery = true)
    int selectCheckByPlanIdAndMemberId(@Param("planId") int planId, @Param("memberId") int memberId);
}
