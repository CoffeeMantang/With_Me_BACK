package com.coffemantang.With_Me_BACK.dto;

import com.coffemantang.With_Me_BACK.model.PlanDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanDetailDTO {

    private int planDetailId;

    private int planId;

    public PlanDetailDTO(final PlanDetail planDetail) {

        this.planDetailId = planDetail.getPlanDetailId();
        this.planId = planDetail.getPlanId();

    }
}
