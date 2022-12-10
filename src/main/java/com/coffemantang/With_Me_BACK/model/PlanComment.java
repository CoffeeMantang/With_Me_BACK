package com.coffemantang.With_Me_BACK.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "plan_comment")
public class PlanComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_comment_id")
    private int planCommentId;

    @Column(name = "plan_id")
    @JoinColumn(name = "plan_id")
    private int planId;

    @Column(name = "member_id")
    @JoinColumn(name = "member_id")
    private int memberId;

    @Column(name = "stage")
    private int stage;

    @Column(name = "turn")
    private int turn;

    @Column(name = "group_num")
    private int groupNum;

    @Column(name = "content")
    private String content;

    @Column(name = "comment_date")
    private LocalDateTime commentDate;

}
