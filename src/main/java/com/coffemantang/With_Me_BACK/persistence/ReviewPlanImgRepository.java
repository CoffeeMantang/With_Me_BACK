package com.coffemantang.With_Me_BACK.persistence;

import com.coffemantang.With_Me_BACK.model.ReviewPlanImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewPlanImgRepository extends JpaRepository<ReviewPlanImg, Integer> {
    List<ReviewPlanImg> findByReviewPlanId(int reviewPlanId);
}
