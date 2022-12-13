package com.coffemantang.With_Me_BACK.controller;

import com.coffemantang.With_Me_BACK.dto.FindPwDTO;
import com.coffemantang.With_Me_BACK.dto.MemberDTO;
import com.coffemantang.With_Me_BACK.dto.ResponseDTO;
import com.coffemantang.With_Me_BACK.model.Member;
import com.coffemantang.With_Me_BACK.persistence.MemberRepository;
import com.coffemantang.With_Me_BACK.security.TokenProvider;
import com.coffemantang.With_Me_BACK.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final PasswordEncoder passwordEncoder;

    private final TokenProvider tokenProvider;

    private final MemberRepository memberRepository;

    // 프로필
    @PostMapping("/profile")
    public ResponseEntity<?> viewProfile(@RequestBody MemberDTO memberDTO) {

        try {
            MemberDTO responseMemberDTO = memberService.viewProfile(memberDTO);
            return ResponseEntity.ok().body(responseMemberDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 내정보
    @GetMapping("/myInfo")
    public ResponseEntity<?> myInfo(@AuthenticationPrincipal String memberId) {

        try {
            MemberDTO temp = MemberDTO.builder().memberId(Integer.parseInt(memberId)).build();
            MemberDTO responseMemberDTO = memberService.viewProfile(temp);
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


    // 비밀번호 찾기 - 질문 가져가기
    @PostMapping("/get-question")
    public ResponseEntity<?> getQuestion(@AuthenticationPrincipal String memberId) {
        try {
            String question = memberService.getQuestion(Integer.parseInt(memberId)); // 아이디로 회원정보 가져옴
            if (question != null && !question.equals("")) {
                final MemberDTO responseMemberDTO = MemberDTO.builder()
                        .question(question)
                        .memberId(Integer.parseInt(memberId)).build();
                return ResponseEntity.ok().body(responseMemberDTO);
            } else {
                // MemberEntity 가져오기 실패 시
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .error("error").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);

        }
    }


    // 비밀번호 찾기 - 답변받고 비밀번호 랜덤으로 초기화해서 돌려주기
    @PostMapping("/find-pw")
    public ResponseEntity<?> findPw(@AuthenticationPrincipal String memberId, @RequestBody MemberDTO memberDTO) {
        try {
            String pw = memberService.checkAnswer(Integer.parseInt(memberId), memberDTO.getAnswer(), passwordEncoder);
            if (pw != null || pw == "") {
                final MemberDTO responseMemberDTO = MemberDTO.builder()
                        .password(pw)
                        .memberId(Integer.parseInt(memberId)).build();
                return ResponseEntity.ok().body(responseMemberDTO);
            } else {
                // MemberEntity 가져오기 실패 시
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .error("error").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);

        }
    }

    // 비밀번호 변경하기 - 기존 비밀번호 체크 후 원하는 비밀번호로 변경하기
    @PostMapping("/change-pw")
    public ResponseEntity<?> chgPw(@AuthenticationPrincipal String memberId, @RequestBody FindPwDTO findPwDTO) {
        try {
            if (memberService.changePw(Integer.parseInt(memberId), findPwDTO.getCurPw(), findPwDTO.getChgPw(), passwordEncoder)) {
                // 변경 성공 시
                ResponseDTO responseDTO = ResponseDTO.builder().error("success").build();
                return ResponseEntity.ok().body(responseDTO);
            } else {
                // 변경 실패 시
                ResponseDTO responseDTO = ResponseDTO.builder().error("error").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 닉네임 수정 - 이미 해당 닉네임이 있는 경우 수정 실패
    @PostMapping("/update-nickname")
    public ResponseEntity<?> updateInfo(@AuthenticationPrincipal String memberId, @RequestBody MemberDTO memberDTO) {
        try {
            String nickname = memberService.updateNickname(Integer.parseInt(memberId), memberDTO.getNickname());
            if (nickname != null || !nickname.equals("")) {
                MemberDTO responseMemberDTO = MemberDTO.builder().nickname(nickname).build();
                return ResponseEntity.ok().body(responseMemberDTO);
            } else {
                ResponseDTO responseDTO = ResponseDTO.builder().error("error").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 이름변경
    @PostMapping("/update-name")
    public ResponseEntity<?> updateName(@AuthenticationPrincipal String memberId,  @RequestBody MemberDTO memberDTO){
        try {
            String name = memberService.updateName(Integer.parseInt(memberId), memberDTO.getName());
            if (name != null || !name.equals("")) {
                MemberDTO responseMemberDTO = MemberDTO.builder().name(name).build();
                return ResponseEntity.ok().body(responseMemberDTO);
            } else {
                ResponseDTO responseDTO = ResponseDTO.builder().error("error").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 본인확인 질문 답변 변경
    @PostMapping("/update-qa")
    public ResponseEntity<?> updateQA(@AuthenticationPrincipal String memberId,  @RequestBody MemberDTO memberDTO){
        try {
            Member memberEntity = memberService.updateQA(Integer.parseInt(memberId), memberDTO.getQuestion(), memberDTO.getAnswer());
            if (memberEntity != null) {
                MemberDTO responseMemberDTO = MemberDTO.builder().question(memberEntity.getQuestion())
                        .answer(memberEntity.getAnswer()).build();
                return ResponseEntity.ok().body(responseMemberDTO);
            } else {
                ResponseDTO responseDTO = ResponseDTO.builder().error("error").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 연락처 변경
    @PostMapping("/update-contact")
    public ResponseEntity<?> updateContact(@AuthenticationPrincipal String memberId, @RequestBody MemberDTO memberDTO) {

        try {
            MemberDTO responseMemberDTO = memberService.updateContact(Integer.parseInt(memberId), memberDTO);
            return ResponseEntity.ok().body(responseMemberDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 프로필 이미지 변경
    @PostMapping("/update-img")
    public ResponseEntity<?> updateProfileImg(@AuthenticationPrincipal String memberId, @RequestBody MemberDTO memberDTO) {

        try {
            MemberDTO responseMemberDTO = memberService.updateProfileImg(Integer.parseInt(memberId), memberDTO);
            return ResponseEntity.ok().body(responseMemberDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 마이페이지
    @PostMapping("/mypage")
    public ResponseEntity<?> viewMyPage(@AuthenticationPrincipal String memberId, @RequestBody MemberDTO memberDTO) {

        try {
            MemberDTO responseMemberDTO = memberService.viewMyPage(Integer.parseInt(memberId), memberDTO);
            return ResponseEntity.ok().body(responseMemberDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 회원정보 수정
    @PostMapping("/editInfo")
    public ResponseEntity<?> registerMember(MemberDTO memberDTO, @AuthenticationPrincipal String memberId) {

        try {
            MemberDTO registeredMember = memberService.editInfo(memberDTO, Integer.parseInt(memberId));
            MemberDTO responseMemberDTO = MemberDTO.builder()
                    .email(registeredMember.getEmail())
                    .nickname(registeredMember.getNickname())
                    .build();
            return ResponseEntity.ok().body(responseMemberDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }


}
