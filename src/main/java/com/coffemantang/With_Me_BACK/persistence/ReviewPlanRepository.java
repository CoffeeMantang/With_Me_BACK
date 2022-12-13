package com.coffemantang.With_Me_BACK.persistence;

import com.coffemantang.With_Me_BACK.model.ReviewPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewPlanRepository extends JpaRepository<ReviewPlan, Integer> {

    // planId와 memberId 에 맞는 리뷰가 있다면 카운트
    int countByPlanIdAndReviewer(int planId, int memberId);

    Page<ReviewPlan> findByReviewer(int reviewer, Pageable pageable);

    // id로 엔티티 가져오기
    ReviewPlan findByReviewPlanId(int reviewPlanId);

}
