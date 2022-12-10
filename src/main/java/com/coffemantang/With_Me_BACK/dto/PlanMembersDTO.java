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

    private int check;

    private String nickname;

    public PlanMembersDTO(final PlanMembers planMembers) {

//        this.planMembersId = planMembers.getPlanMembersId();
        this.planId = planMembers.getPlanId();
        this.memberId = planMembers.getMemberId();
        this.check = planMembers.getCheck();

    }

}
