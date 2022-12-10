package com.coffemantang.With_Me_BACK.controller;

import com.coffemantang.With_Me_BACK.dto.CheckDTO;
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
//    @PostMapping("/check")
//    public ResponseEntity<?> checkCondition(@AuthenticationPrincipal String memberId, @RequestBody PlanDTO planDTO) {
//
//        try {
//            planService.checkCondition(planDTO);
//            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
//            return ResponseEntity.ok().body(responseDTO);
//        } catch (Exception e) {
//            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
//            return ResponseEntity.badRequest().body(responseDTO);
//        }
//
//    }

    // 여행 일정 추가
    @PostMapping("/add")
    public ResponseEntity<?> addPlan(@AuthenticationPrincipal String memberId, @RequestBody PlanDTO planDTO) {

        try {
            PlanDTO responsePlanDTO = planService.addPlan(Integer.parseInt(memberId), planDTO);
            return ResponseEntity.ok().body(responsePlanDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    @PostMapping("/test")
    public ResponseEntity<?> test(@AuthenticationPrincipal String memberId, @RequestBody CheckDTO checkDTO) {

        try {
            PlanDTO planDTO = new PlanDTO();
            planDTO.setPlanId(checkDTO.getPlanId());
            CheckDTO newchd = new CheckDTO(planDTO);
            CheckDTO checkDTO1 = planService.checkState(newchd);
            return ResponseEntity.ok().body(checkDTO1);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }
}
