package com.coffemantang.With_Me_BACK.controller;

import com.coffemantang.With_Me_BACK.dto.PlanDTO;
import com.coffemantang.With_Me_BACK.dto.PlanDetailDTO;
import com.coffemantang.With_Me_BACK.dto.ResponseDTO;
import com.coffemantang.With_Me_BACK.service.PlanDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/plan/detail")
public class PlanDetailController {

    private final PlanDetailService planDetailService;

    // 여행 일정 디테일 추가
    @PostMapping("/add")
    public ResponseEntity<?> addPlan(@AuthenticationPrincipal String memberId, PlanDTO planDetailDTOList) {

        try {
            planDetailService.addDetail(Integer.parseInt(memberId), planDetailDTOList);
            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 여행 일정 디테일 수정
    @PostMapping("/update")
    public ResponseEntity<?> updateDetail(@AuthenticationPrincipal String memberId, PlanDetailDTO planDetailDTOList) {

        try {
            planDetailService.updateOneDetail(Integer.parseInt(memberId), planDetailDTOList);
            return ResponseEntity.ok().body("ok");
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 여행 일정 디테일 가져오기
    @GetMapping("/view")
    public ResponseEntity<?> viewDetail(@RequestParam int planDetailId) throws Exception{
        try{
            PlanDetailDTO planDetailDTO = planDetailService.getPlanDetail(planDetailId);
            return ResponseEntity.ok().body(planDetailDTO);
        }catch (Exception e) {
            e.printStackTrace();
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

}
