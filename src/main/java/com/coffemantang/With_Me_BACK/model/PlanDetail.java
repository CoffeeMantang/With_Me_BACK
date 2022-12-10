package com.coffemantang.With_Me_BACK.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "plan_detail")
public class PlanDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_detail_id")
    private int planDetailId;

    @Column(name = "plan_id")
    @JoinColumn(name = "plan_id")
    private int planId;

    @Column(name = "detail_date")
    private LocalDate detailDate;

    @Column(name = "content")
    private String content;

    @Column(name = "detail_img")
    private String detailImg;

}
