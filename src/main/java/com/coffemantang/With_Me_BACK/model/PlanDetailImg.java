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
@Table(name = "plan_detail_img")
public class PlanDetailImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_detail_img_id")
    private int planDetailImgId;

    @Column(name = "plan_detail_id")
    @JoinColumn(name = "plan_detail_id")
    private int planDetailId;

    @Column(name = "path")
    private String path;

}
