package com.coffemantang.With_Me_BACK.dto;

import com.coffemantang.With_Me_BACK.model.ReviewPlanImg;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPlanImgDTO {

    private int reviewPlanImgId;

    private int reviewPlanId;

    private String path;

    public ReviewPlanImgDTO(final ReviewPlanImg reviewPlanImg) {

        this.reviewPlanImgId = reviewPlanImg.getReviewPlanImgId();
        this.reviewPlanId = reviewPlanImg.getReviewPlanId();
        this.path = reviewPlanImg.getPath();

    }

}
