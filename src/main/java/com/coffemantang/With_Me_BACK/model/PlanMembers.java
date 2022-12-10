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
@Table(name = "plan_members")
public class PlanMembers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_members_id")
    private int planMembersId;

    @Column(name = "plan_id")
    @JoinColumn(name = "plan_id")
    private int planId;

    @Column(name = "member_id")
    @JoinColumn(name = "member_id")
    private int memberId;

    @Column(name = "check")
    private int check;

}
