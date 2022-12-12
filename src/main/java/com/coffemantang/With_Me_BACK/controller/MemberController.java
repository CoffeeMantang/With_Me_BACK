package com.coffemantang.With_Me_BACK.controller;

import com.coffemantang.With_Me_BACK.dto.MemberDTO;
import com.coffemantang.With_Me_BACK.dto.PlanDTO;
import com.coffemantang.With_Me_BACK.dto.ResponseDTO;
import com.coffemantang.With_Me_BACK.model.Member;
import com.coffemantang.With_Me_BACK.persistence.MemberRepository;
import com.coffemantang.With_Me_BACK.security.TokenProvider;
import com.coffemantang.With_Me_BACK.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final TokenProvider tokenProvider;
    @Autowired
    private final MemberRepository memberRepository;



    // 프로필
    @PostMapping("/profile")
    public ResponseEntity<?> viewProfile(@AuthenticationPrincipal String memberId, @RequestBody MemberDTO memberDTO) {

        try {
            MemberDTO responseMemberDTO = memberService.viewProfile(Integer.parseInt(memberId), memberDTO);
            return ResponseEntity.ok().body(responseMemberDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 토큰 갱신하기
    @GetMapping("/updateToken")
    public ResponseEntity<?> updateToken(@AuthenticationPrincipal String memberId){
        try{
            Member member = memberRepository.findByMemberId(Integer.parseInt(memberId));
            MemberDTO memberDTO = MemberDTO.builder().memberId(member.getMemberId())
                    .password(member.getEmail()).build();
            final String token = tokenProvider.create(memberDTO);
            MemberDTO responseMemberDTO = MemberDTO.builder()
                    .token(token)
                    .build();
            return ResponseEntity.ok().body(responseMemberDTO);
        }catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
