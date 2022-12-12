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
@Table(name = "plan")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private int planId;

    @Column(name = "member_id")
    @JoinColumn(name = "member_id")
    private int memberId;

    @Column(name = "title")
    private String title;

    @Column(name = "state")
    private int state; // 0 : 모집 중 / 1 : 모집 완료 / 2 : 여행 시작 / 3: 여행 종료

    @Column(name = "personnel")
    private int personnel; // 모집인원

    @Column(name = "post_date")
    private LocalDateTime postDate; // 게시일

    @Column(name = "deadline")
    private LocalDateTime deadline; // 마감일

    @Column(name = "start_date")
    private LocalDateTime startDate; // 여행 시작일

    @Column(name = "end_date")
    private LocalDateTime endDate; // 여행 종료일

    @Column(name = "notice")
    private String notice;

    @Column(name = "theme")
    private String theme; // 테마

    @Column(name = "hit")
    private int hit; // 조회수

    @Column(name = "place")
    private String place;
}
