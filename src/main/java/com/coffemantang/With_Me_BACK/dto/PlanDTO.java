package com.coffemantang.With_Me_BACK.dto;

import com.coffemantang.With_Me_BACK.model.Plan;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanDTO {

    private int planId;

    private int memberId;

    private String title;

    private int state;

    private int personnel;

    private LocalDateTime postDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deadline;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    private String notice;

    private String theme;

    private int hit;

    private List<PlanDetailDTO> planDetailDTOList;

    public PlanDTO(final Plan plan) {

        this.planId = plan.getPlanId();
        this.memberId = plan.getMemberId();
        this.title = plan.getTitle();
        this.state = plan.getState();
        this.personnel = plan.getPersonnel();
        this.postDate = plan.getPostDate();
        this.deadline = plan.getDeadline();
        this.startDate = plan.getStartDate();
        this.endDate = plan.getEndDate();
        this.notice = plan.getNotice();
        this.theme = plan.getTheme();
        this.hit = plan.getHit();

    }
}
