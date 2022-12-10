package com.coffemantang.With_Me_BACK.dto;

import com.coffemantang.With_Me_BACK.model.Plan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckDTO {

    private List<PlanDTO> planDTOList;

    private PlanDTO planDTO;

    private int planId;

    public CheckDTO(PlanDTO planDTO) {
        this.planDTO = planDTO;
    }
    public CheckDTO(List<PlanDTO> planDTOList) {
        this.planDTOList = planDTOList;
    }
    public CheckDTO(int planId) {
        this.planId = planId;
    }
}
