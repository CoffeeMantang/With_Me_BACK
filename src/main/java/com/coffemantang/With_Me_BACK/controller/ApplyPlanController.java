package com.coffemantang.With_Me_BACK.controller;

import com.coffemantang.With_Me_BACK.dto.ApplyPlanDTO;
import com.coffemantang.With_Me_BACK.dto.ResponseDTO;
import com.coffemantang.With_Me_BACK.service.ApplyPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/apply-plan")
public class ApplyPlanController {

    private final ApplyPlanService applyPlanService;

    // 여행 참여 신청
    @PostMapping("/apply")
    public ResponseEntity<?> applyPlan(@AuthenticationPrincipal String memberId, @RequestBody ApplyPlanDTO applyPlanDTO) {

        try {
            applyPlanService.applyPlan(Integer.parseInt(memberId), applyPlanDTO);
            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 신청 수락/거절
    @PostMapping("/accept-refuse")
    public ResponseEntity<?> acceptRefuse(@AuthenticationPrincipal String memberId, @RequestBody ApplyPlanDTO applyPlanDTO, @RequestParam int isAccept) {

        try {
            applyPlanService.acceptRefuse(Integer.parseInt(memberId), applyPlanDTO, isAccept);
            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 신청 취소
    @PostMapping("/cancel")
    public ResponseEntity<?> cancelApply(@AuthenticationPrincipal String memberId, @RequestBody ApplyPlanDTO applyPlanDTO) {

        try {
            applyPlanService.cancelApply(Integer.parseInt(memberId), applyPlanDTO);
            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }
}
