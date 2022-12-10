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
@Table(name = "review_plan_img")
public class ReviewPlanImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_plan_img_id")
    private int reviewPlanImgId;

    @Column(name = "review_plan_id")
    @JoinColumn(name = "review_plan_id")
    private int reviewPlanId;

    @Column(name = "path")
    private String path;

}
