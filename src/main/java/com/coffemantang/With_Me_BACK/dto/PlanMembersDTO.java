package com.coffemantang.With_Me_BACK.dto;

import com.coffemantang.With_Me_BACK.model.PlanMembers;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanMembersDTO {

    private int planMembersId;

    private int planId;

    private int memberId;

    private int checkReview;

    private String nickname;

    private int possibleReviewMember; // 0 : 작성 안 함 1 : 작성함

    public PlanMembersDTO(final PlanMembers planMembers) {

//        this.planMembersId = planMembers.getPlanMembersId();
        this.planId = planMembers.getPlanId();
        this.memberId = planMembers.getMemberId();
        this.checkReview = planMembers.getCheckReview();

    }

}
