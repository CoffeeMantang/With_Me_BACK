package com.coffemantang.With_Me_BACK.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "apply_plan")
public class ApplyPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apply_plan_id")
    private int applyPlanId;

    @Column(name = "plan_id")
    @JoinColumn(name = "plan_id")
    private int planId;

    @Column(name = "member_id")
    @JoinColumn(name = "member_id")
    private int memberId;

    @Column(name = "content")
    private String content;

    @Column(name = "state")
    private int state; // 0 : 신청 / 1 : 수락 / 2 : 거절 / 3 : 취소

}
