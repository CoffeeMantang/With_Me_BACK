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
@Table(name = "review_member")
public class ReviewMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_member_id")
    private int reviewMemberId;

    @Column(name = "reviewer")
    @JoinColumn(name = "member_id")
    private int reviewer; // 리뷰 작성자

    @Column(name = "reviewed")
    @JoinColumn(name = "member_id")
    private int reviewed; // 리뷰 대상자

    @Column(name = "plan_id")
    @JoinColumn(name = "plan_id")
    private int planId;

    @Column(name = "rating")
    private double rating; // 별점

    @Column(name = "content")
    private String content;

}
