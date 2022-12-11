package com.coffemantang.With_Me_BACK.controller;

import com.coffemantang.With_Me_BACK.dto.PlanDTO;
import com.coffemantang.With_Me_BACK.dto.ResponseDTO;
import com.coffemantang.With_Me_BACK.dto.ReviewPlanDTO;
import com.coffemantang.With_Me_BACK.service.ReviewPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/review/plan")
public class ReviewPlanController {

    private final ReviewPlanService reviewPlanService;

    // 여행 리뷰 작성
    @PostMapping("/add")
    public ResponseEntity<?> addReviewPlan(@AuthenticationPrincipal String memberId, ReviewPlanDTO reviewPlanDTO) {

        try {
            reviewPlanService.addReviewPlan(Integer.parseInt(memberId), reviewPlanDTO);
            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 프로필 대상이 쓴 여행 리뷰 리스트
    @PostMapping("/list")
    public ResponseEntity<?> listReviewPlan(@RequestParam int targetMemberId, @PageableDefault(size = 10) Pageable pageable) {

        try {
            List<ReviewPlanDTO> reviewPlanDTOList = reviewPlanService.listReviewPlan(targetMemberId, pageable);
            return ResponseEntity.ok().body(reviewPlanDTOList);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }



    // 여행 리뷰 보기
//    @PostMapping("/view")
//    public ResponseEntity<?> viewReviewPlan(@RequestParam int targetMemberId, @RequestBody ReviewPlanDTO reviewPlanDTO) {
//
//        try {
//            ReviewPlanDTO responseDTO = reviewPlanService.viewReviewPlan(targetMemberId, reviewPlanDTO);
//            return ResponseEntity.ok().body(responseDTO);
//        } catch (Exception e) {
//            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
//            return ResponseEntity.badRequest().body(responseDTO);
//        }
//
//    }

}
