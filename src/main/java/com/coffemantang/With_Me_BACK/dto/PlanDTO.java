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

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
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

    private String place;
    
    private String nickname; // 작성자 닉네임

    private long participant; // 참가한 사람 수

    private int reviewPlanState; // 여행 리뷰 상태 0 : 불가능(여행 끝나지 않음), 1 : 가능(여행 끝남), 2 : 완료

    private int reviewMemberState; // 구성원 평가 상태 0 : 불가능, 1: 가능, 2 : 완료

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
        this.place = plan.getPlace();

    }
}
