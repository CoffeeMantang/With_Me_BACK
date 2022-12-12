package com.coffemantang.With_Me_BACK.controller;

import com.coffemantang.With_Me_BACK.dto.MemberDTO;
import com.coffemantang.With_Me_BACK.dto.ResponseDTO;
import com.coffemantang.With_Me_BACK.security.TokenProvider;
import com.coffemantang.With_Me_BACK.service.EmailTokenService;
import com.coffemantang.With_Me_BACK.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/non-member")
public class NonMemberController {

    private final PasswordEncoder passwordEncoder;

    private final EmailTokenService emailTokenService;

    private final MemberService memberService;

    private final TokenProvider tokenProvider;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> registerMember(MemberDTO memberDTO) {

        try {
            MemberDTO registeredMember = memberService.add(memberDTO);
            MemberDTO responseMemberDTO = MemberDTO.builder()
                    .email(registeredMember.getEmail())
                    .nickname(registeredMember.getNickname())
                    .build();
//            emailTokenService.createEmailToken(registeredMember.getMemberId(), registeredMember.getEmail()); // 이메일 전송
//            return ResponseEntity.ok().body(responseMemberDTO);
            return ResponseEntity.ok().body(registeredMember);
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


}
