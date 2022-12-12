package com.coffemantang.With_Me_BACK.dto;

import com.coffemantang.With_Me_BACK.model.PlanDetail;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanDetailDTO {

    private int planDetailId;

    private int planId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate detailDate;

    private String content;

    private String detailImg;

    private List<MultipartFile> file;


    public PlanDetailDTO(final PlanDetail planDetail) {

        this.planDetailId = planDetail.getPlanDetailId();
        this.planId = planDetail.getPlanId();
        this.detailDate = planDetail.getDetailDate();
        this.content = planDetail.getContent();
        this.detailImg = planDetail.getDetailImg();

    }

    // 파일 null 체크
    public boolean checkFileNull() {
        return this.file != null;
    }

}
