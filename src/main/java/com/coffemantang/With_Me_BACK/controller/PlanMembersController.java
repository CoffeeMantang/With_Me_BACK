package com.coffemantang.With_Me_BACK.controller;

import com.coffemantang.With_Me_BACK.dto.PlanDTO;
import com.coffemantang.With_Me_BACK.dto.PlanMembersDTO;
import com.coffemantang.With_Me_BACK.dto.ResponseDTO;
import com.coffemantang.With_Me_BACK.service.PlanMembersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/plan-members")
public class PlanMembersController {

    private final PlanMembersService planMembersService;

    // 여행 구성원 보기
    @PostMapping("/view")
    public ResponseEntity<?> viewPlanMembers(@AuthenticationPrincipal String memberId, @RequestBody PlanDTO planDTO) {

        try {
            List<PlanMembersDTO> planMembersDTOList = planMembersService.viewPlanMembers(Integer.parseInt(memberId), planDTO);
            return ResponseEntity.ok().body(planMembersDTOList);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 여행 구성원 내보내기
    @PostMapping("/out")
    public ResponseEntity<?> outPlanMembers(@AuthenticationPrincipal String memberId, @RequestBody PlanMembersDTO planMembersDTO) {

        try {
            planMembersService.outPlanMembers(Integer.parseInt(memberId), planMembersDTO);
            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }


}
