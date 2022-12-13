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
            planService.updatePlan2(Integer.parseInt(memberId), planDTO);
            return ResponseEntity.ok().body("ok");
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

    // 나의 여행 일정 리스트 - 마이페이지
    @PostMapping("/list")
    public ResponseEntity<?> listPlan(@AuthenticationPrincipal String memberId, @PageableDefault(size = 10) Pageable pageable) {

        try {
            List<PlanDTO> responsePlanDTOList = planService.listPlan(Integer.parseInt(memberId), pageable);
            return ResponseEntity.ok().body(responsePlanDTOList);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 내가 쓴 글인지 확인
    @GetMapping("/isMyPlan")
    public ResponseEntity<?> isMyPlan(@AuthenticationPrincipal String memberId, @RequestParam int planId) throws Exception{
        try{
            long cnt = planService.isMyPlan(Integer.parseInt(memberId), planId);
            return ResponseEntity.ok().body(cnt);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

}
