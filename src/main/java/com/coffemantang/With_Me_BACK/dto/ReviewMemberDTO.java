package com.coffemantang.With_Me_BACK.dto;

import com.coffemantang.With_Me_BACK.model.ReviewMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewMemberDTO {

    private int reviewMemberId;

    private int reviewer;

    private int reviewed;

    private int planId;

    private double rating;

    private String content;

    public ReviewMemberDTO(final ReviewMember reviewMember) {

        this.reviewMemberId = reviewMember.getReviewMemberId();
        this.reviewer = reviewMember.getReviewer();
        this.reviewed = reviewMember.getReviewed();
        this.planId = reviewMember.getPlanId();
        this.rating = reviewMember.getRating();
        this.content = reviewMember.getContent();

    }

}
