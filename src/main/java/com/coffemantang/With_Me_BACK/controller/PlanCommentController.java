package com.coffemantang.With_Me_BACK.controller;

import com.coffemantang.With_Me_BACK.dto.PlanCommentDTO;
import com.coffemantang.With_Me_BACK.dto.ResponseDTO;
import com.coffemantang.With_Me_BACK.service.PlanCommentService;
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
@RequestMapping("/plan/comment")
public class PlanCommentController {

    private final PlanCommentService planCommentService;

    // 댓글 입력
    @PostMapping("/add-comment")
    public ResponseEntity<?> addComment(@AuthenticationPrincipal String memberId, @RequestBody PlanCommentDTO planCommentDTO, @RequestParam int planId) {

        try {
            planCommentService.addComment(Integer.parseInt(memberId), planCommentDTO, planId);
            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 대댓글 입력
    @PostMapping("/add-recomment")
    public ResponseEntity<?> addRecomment(@AuthenticationPrincipal String memberId, @RequestBody PlanCommentDTO planCommentDTO ) {
//    public ResponseEntity<?> addRecomment(@AuthenticationPrincipal String memberId, @RequestBody PlanCommentDTO planCommentDTO) {

        try {
            String content = planCommentDTO.getContent();
            planCommentService.addRecomment(Integer.parseInt(memberId), planCommentDTO, content);
            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }


}
