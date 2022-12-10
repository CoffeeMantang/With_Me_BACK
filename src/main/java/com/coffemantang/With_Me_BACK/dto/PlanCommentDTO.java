package com.coffemantang.With_Me_BACK.dto;

import com.coffemantang.With_Me_BACK.model.PlanComment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanCommentDTO {

    private int planCommentId;

    private int planId;

    private int memberId;

    private int stage;

    private int turn;

    private int groupNum;

    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime commentDate;

    private String nickname;

    public PlanCommentDTO(final PlanComment planComment) {

        this.planCommentId = planComment.getPlanCommentId();
        this.planId = planComment.getPlanId();
        this.memberId = planComment.getMemberId();
        this.stage = planComment.getStage();
        this.turn = planComment.getTurn();
        this.groupNum = planComment.getGroupNum();
        this.content = planComment.getContent();
        this.commentDate = planComment.getCommentDate();

    }
}
