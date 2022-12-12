package com.coffemantang.With_Me_BACK.persistence;

import com.coffemantang.With_Me_BACK.model.PlanComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanCommentRepository extends JpaRepository<PlanComment, Integer> {

    // 가장 큰 turn 값 가져오기
    @Query(value = "SELECT MAX(turn) FROM plan_comment WHERE plan_id = :planId AND group_num = :groupNum", nativeQuery = true)
    Integer selectMaxTurnByPlanIdAndGroupNum(@Param("planId") int planId,@Param("groupNum") int groupNum);

    // 대댓글 리스트 가져오기
    Page<PlanComment> findByPlanIdAndGroupNumAndStageOrderByTurn(int planId, int groupNum, int stage, Pageable pageable);

    // 댓글 리스트 가져오기
    Page<PlanComment> findByPlanIdAndStageOrderByCommentDate(int planId, int stage, Pageable pageable);

    // 대댓글 갯수 가져오기
    long countByGroupNum(int groupNum);
}
