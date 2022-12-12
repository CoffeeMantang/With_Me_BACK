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
    @Query(value = "SELECT COUNT(*) FROM plan p INNER JOIN plan_members pm ON p.plan_id = pm.plan_id WHERE pm.member_id = :memberId AND pm.check_review = :checkReview", nativeQuery = true)
    int selectCountByMemberIdAndCheckReview(@Param("memberId") int memberId, @Param("checkReview") int checkReview);

    // 여행 횟수 가져오기
    @Query(value = "SELECT COUNT(*) FROM plan_members WHERE member_id = :memberId AND check_review > :checkReview", nativeQuery = true)
    int selectCountByMemberIdAndCheckReviewGreaterThan(@Param("memberId") int memberId, @Param("checkReview") int checkReview);

    // planId와 memberId로 구성원에 있는지 확인
    long countByPlanIdAndMemberId(int planId, int memberId);

    // planId와 memberId로 check 가져오기
    @Query(value = "SELECT check_review FROM plan_members WHERE plan_id = :planId AND member_id = :memberId", nativeQuery = true)
    int selectCheckByPlanIdAndMemberId(@Param("planId") int planId, @Param("memberId") int memberId);

    // 해당 memberId를 제외한 planId에 맞는 count
    @Query(value = "SELECT COUNT(*) FROM plan_members WHERE plan_id = :planId AND not member_id = :memberId", nativeQuery = true)
    int countByPlanIdAndNotMemberId(@Param("planId") int planId, @Param("memberId") int memberId);

    // planId와 memberId에 맞는 entity
    PlanMembers findByPlanIdAndMemberId(int planId, int memberId);

    // p.state가 3, pm.check가 0이고 pm.memberId에 맞는 로우의 check를 1로 수정
    @Query(value = "UPDATE plan p INNER JOIN plan_members AS pm ON p.plan_id = pm.plan_id SET pm.check_review = 1 WHERE p.state = 3 AND pm.member_id = :memberId and pm.check_review = 0", nativeQuery = true)
    void updateCheck1(@Param("memberId") int memberId);

    // planId, memberId에 맞는 로우의 check 수정
    @Query(value = "UPDATE plan_members SET check_review = :checkReview WHERE plan_id = :planId AND member_id = :memberId", nativeQuery = true)
    void updateCheck2(@Param("planId") int planId, @Param("memberId") int memberId, @Param("checkReview") int checkReview);

    long countByPlanId(int planId);

    // 나를 제외한 구성원 리스트
    @Query(value = "SELECT * FROM plan_members WHERE plan_id = :planId AND not member_id = :memberId", nativeQuery = true)
    List<PlanMembers> findByPlanIdNotMemberId(@Param("planId") int planId, @Param("memberId") int memberId);
}
