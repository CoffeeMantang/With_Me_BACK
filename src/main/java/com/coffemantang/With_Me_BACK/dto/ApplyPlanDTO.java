package com.coffemantang.With_Me_BACK.dto;

import com.coffemantang.With_Me_BACK.model.ApplyPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyPlanDTO {

    private int applyPlanId;

    private int planId;

    private int memberId;

    private String content;

    private int state;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    public ApplyPlanDTO(final ApplyPlan applyPlan) {

        this.applyPlanId = applyPlan.getApplyPlanId();
        this.planId = applyPlan.getPlanId();
        this.memberId = applyPlan.getMemberId();
        this.content = applyPlan.getContent();
        this.state = applyPlan.getState();

    }

}
