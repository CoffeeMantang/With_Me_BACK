package com.coffemantang.With_Me_BACK.controller;

import com.coffemantang.With_Me_BACK.dto.ApplyPlanDTO;
import com.coffemantang.With_Me_BACK.dto.PlanDTO;
import com.coffemantang.With_Me_BACK.dto.ResponseDTO;
import com.coffemantang.With_Me_BACK.service.PlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/plan")
public class PlanController {

    private final PlanService planService;

    // 조건 체크
    @PostMapping("/check")
    public ResponseEntity<?> checkCondition(@AuthenticationPrincipal String memberId, @RequestBody PlanDTO planDTO) {

        try {
            int check = planService.checkCondition(Integer.parseInt(memberId), planDTO);
            ResponseDTO responseDTO = ResponseDTO.builder().error(String.valueOf(check)).build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 여행 일정 추가
//    @PostMapping("/add")
//    public ResponseEntity<?> addPlan(@AuthenticationPrincipal String memberId, @RequestBody PlanDTO planDTO) {
//
//        try {
//            planService.checkCondition(Integer.parseInt(memberId), planDTO);
//            ResponseDTO responseDTO = ResponseDTO.builder().error(String.valueOf(check)).build();
//            return ResponseEntity.ok().body(responseDTO);
//        } catch (Exception e) {
//            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
//            return ResponseEntity.badRequest().body(responseDTO);
//        }
//
//    }
}
