package com.coffemantang.With_Me_BACK.dto;

import com.coffemantang.With_Me_BACK.model.ReviewPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPlanDTO {

    private int reviewPlanId;

    private int reviewer;

    private int planId;

    private double rating;

    private String content;

    private List<MultipartFile> files;

    private List<String> reviewFiles;

    private List<String> images;

    public ReviewPlanDTO(final ReviewPlan reviewPlan) {

        this.reviewPlanId = reviewPlan.getReviewPlanId();
        this.reviewer = reviewPlan.getReviewer();
        this.planId = reviewPlan.getPlanId();
        this.rating = reviewPlan.getRating();
        this.content = reviewPlan.getContent();

    }

}
