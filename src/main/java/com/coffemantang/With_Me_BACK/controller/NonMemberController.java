package com.coffemantang.With_Me_BACK.controller;

import com.coffemantang.With_Me_BACK.dto.*;
import com.coffemantang.With_Me_BACK.security.TokenProvider;
import com.coffemantang.With_Me_BACK.service.EmailTokenService;
import com.coffemantang.With_Me_BACK.service.MemberService;
import com.coffemantang.With_Me_BACK.service.PlanCommentService;
import com.coffemantang.With_Me_BACK.service.PlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/non-member")
public class NonMemberController {

    private final PasswordEncoder passwordEncoder;

    private final EmailTokenService emailTokenService;

    private final MemberService memberService;

    private final TokenProvider tokenProvider;

    private final PlanCommentService planCommentService;

    @Autowired
    private final PlanService planService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> registerMember(MemberDTO memberDTO) {

        try {
            MemberDTO registeredMember = memberService.add(memberDTO);
            MemberDTO responseMemberDTO = MemberDTO.builder()
                    .email(registeredMember.getEmail())
                    .nickname(registeredMember.getNickname())
                    .build();
            emailTokenService.createEmailToken(registeredMember.getMemberId(), registeredMember.getEmail()); // 이메일 전송
            return ResponseEntity.ok().body(responseMemberDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 로그인
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody MemberDTO memberDTO) {
        // 로그인 성공 시에만 MemberEntity 가져옴
        MemberDTO successMemberDTO = memberService.getByCredentials(
                memberDTO.getEmail(),
                memberDTO.getPassword(),
                passwordEncoder
        );
        // MemberEntity 가져오기 성공 시
        if (successMemberDTO != null) {

            // TokenProvider 클래스를 이용해 토큰을 생성한 후 MemberDTO에 넣어서 반환
            final String token = tokenProvider.create(successMemberDTO);
            MemberDTO responseMemberDTO = MemberDTO.builder()
                    .email(successMemberDTO.getEmail())
                    .token(token)
                    .memberId(successMemberDTO.getMemberId())
                    .nickname(successMemberDTO.getNickname())
                    .build();
            return ResponseEntity.ok().body(responseMemberDTO);

        } else {
            // MemberEntity 가져오기 실패 시 -> 로그인 실패
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .error("login failed").build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 여행 일정 찾기
    @GetMapping("/search")
    public ResponseEntity<?> searchPlan(@RequestParam(value="keyword") String keyword, @PageableDefault(size = 10) Pageable pageable) throws Exception{
        try{
            List<PlanDTO> result = planService.planSearch(keyword, pageable);
            return ResponseEntity.ok().body(result);
        }catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 여행 일정 보기
    @GetMapping("/view")
    public ResponseEntity<?> viewPlan(PlanDTO planDTO) {

        try {
            PlanDTO responsePlanDTO = planService.viewPlan(planDTO);
            return ResponseEntity.ok().body(responsePlanDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }


    // 댓글 리스트
    @PostMapping("/list-comment")
    public ResponseEntity<?> listcomment( PlanCommentDTO planCommentDTO, @PageableDefault(size = 10) Pageable pageable) {

        try {
            List<PlanCommentDTO> planCommentDTOList = planCommentService.listComment(planCommentDTO, pageable);
            return ResponseEntity.ok().body(planCommentDTOList);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 대댓글 리스트
    @PostMapping("/list-recomment")
    public ResponseEntity<?> listRecomment( @RequestBody PlanCommentDTO planCommentDTO, @PageableDefault(size = 10) Pageable pageable) {

        try {
            List<PlanCommentDTO> planCommentDTOList = planCommentService.listRecomment(planCommentDTO, pageable);
            return ResponseEntity.ok().body(planCommentDTOList);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 닉네임 중복 체크
    @PostMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestBody MemberDTO memberDTO){
        try{
            boolean check = memberService.checkNickname(memberDTO.getNickname());
            if(check){
                MemberDTO responseMemberDTO = MemberDTO.builder().nickname(memberDTO.getNickname()).build();
                return ResponseEntity.ok().body(responseMemberDTO);
            }else{
                ResponseDTO responseDTO = ResponseDTO.builder().error("error").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        }catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }


    // 이메일 중복 체크
    @PostMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestBody MemberDTO memberDTO){
        try{
            if(memberService.checkEmail(memberDTO.getEmail())){
                ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
                return ResponseEntity.ok().body(responseDTO);
            }else{
                ResponseDTO responseDTO = ResponseDTO.builder().error("error").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        }catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 인증 이메일 재전송
    @PostMapping("/reconfirm")
    public ResponseEntity<?> viewConfirmEmail(@RequestBody MemberDTO memberDTO){
        try{
            emailTokenService.createEmailToken(memberDTO.getMemberId(), memberDTO.getEmail()); // 이메일 전송
            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
            return ResponseEntity.ok().body(responseDTO);
        }catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
