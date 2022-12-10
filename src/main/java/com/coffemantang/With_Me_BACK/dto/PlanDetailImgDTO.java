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
public class PlanDetailImgDTO {

    private int planDetailImgId;

    private int planDetailId;

    private String path;

    public PlanDetailImgDTO(final PlanDetailImgDTO planDetailImgDTO) {

        this.planDetailImgId = planDetailImgDTO.getPlanDetailId();
        this.planDetailId = planDetailImgDTO.getPlanDetailId();
        this.path = planDetailImgDTO.getPath();

    }

}
