package com.coffemantang.With_Me_BACK.controller;

import com.coffemantang.With_Me_BACK.dto.PlanDTO;
import com.coffemantang.With_Me_BACK.dto.ResponseDTO;
import com.coffemantang.With_Me_BACK.service.PlanService;
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
@RequestMapping("/plan")
public class PlanController {

    private final PlanService planService;

    // 여행 일정 추가
    @PostMapping(value="/add")
    public ResponseEntity<?> addPlan(@AuthenticationPrincipal String memberId, PlanDTO planDTO) {

        try {
            PlanDTO responsePlanDTO = planService.addPlan(Integer.parseInt(memberId), planDTO);
            return ResponseEntity.ok().body(responsePlanDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 여행 일정 수정
    @PostMapping("/update")
    public ResponseEntity<?> updatePlan(@AuthenticationPrincipal String memberId, @RequestBody PlanDTO planDTO) {

        try {
            PlanDTO responsePlanDTO = planService.updatePlan(Integer.parseInt(memberId), planDTO);
            return ResponseEntity.ok().body(responsePlanDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 여행 일정 삭제
    @PostMapping("/delete")
    public ResponseEntity<?> deletePlan(@AuthenticationPrincipal String memberId, @RequestBody PlanDTO planDTO) {

        try {
            planService.deletePlan(Integer.parseInt(memberId), planDTO);
            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 여행 일정 보기
    @PostMapping("/view")
    public ResponseEntity<?> viewPlan(@RequestBody PlanDTO planDTO) {

        try {
            PlanDTO responsePlanDTO = planService.viewPlan(planDTO);
            return ResponseEntity.ok().body(responsePlanDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }



    // 여행 일정 리스트
//    @PostMapping("/list")
//    public ResponseEntity<?> listPlan(@PageableDefault(size = 10) Pageable pageable) {
//
//        try {
//            List<PlanDTO> responsePlanDTOList = planService.listPlan(pageable);
//            return ResponseEntity.ok().body(responsePlanDTOList);
//        } catch (Exception e) {
//            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
//            return ResponseEntity.badRequest().body(responseDTO);
//        }
//
//    }

}
