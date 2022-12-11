package com.coffemantang.With_Me_BACK.persistence;

import com.coffemantang.With_Me_BACK.model.ReviewMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewMemberRepository extends JpaRepository<ReviewMember, Integer> {

    // reviewMemberId로 엔티티 가져오기
    ReviewMember findByReviewMemberId(int reviewMemberId);

    // reviewed 아이디로 평점 평균 가져오기
    @Query(value = "SELECT AVG(rating) FROM review_member WHERE reviewed = :reviewed", nativeQuery = true)
    double selectAVGRatingByReviewed(int reviewed);

    // 페이징
    Page<ReviewMember> findByReviewed(int memberId, Pageable pageable);

    // planId, reviewer, reviewed에 맞는 평가 count
    int countByPlanIdAndReviewerAndReviewed(int planId, int reviewer, int reviewed);

    // planId와 reviewer에 맞는 count
    int countByPlanIdAndReviewer(int planId, int reviewer);
}
