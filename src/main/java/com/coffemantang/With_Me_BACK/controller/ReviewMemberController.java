package com.coffemantang.With_Me_BACK.controller;

import com.coffemantang.With_Me_BACK.dto.ResponseDTO;
import com.coffemantang.With_Me_BACK.dto.ReviewMemberDTO;
import com.coffemantang.With_Me_BACK.service.ReviewMemberService;
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
@RequestMapping("/review/member")
public class ReviewMemberController {

    private final ReviewMemberService reviewMemberService;

    // 평가 작성
    @PostMapping("/add")
    public ResponseEntity<?> addReview(@AuthenticationPrincipal String memberId, @RequestBody List<ReviewMemberDTO> reviewMemberDTOList) {

        try {
            reviewMemberService.addReview(Integer.parseInt(memberId), reviewMemberDTOList);
            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 평가 수정
    @PostMapping("/update")
    public ResponseEntity<?> updateReview(@AuthenticationPrincipal String memberId, @RequestBody List<ReviewMemberDTO> reviewMemberDTOList) {

        try {
            List<ReviewMemberDTO> responseDTOList = reviewMemberService.updateReview(Integer.parseInt(memberId), reviewMemberDTOList);
            return ResponseEntity.ok().body(responseDTOList);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }



}
