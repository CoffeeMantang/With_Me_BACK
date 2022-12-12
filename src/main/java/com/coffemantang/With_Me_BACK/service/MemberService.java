package com.coffemantang.With_Me_BACK.service;

import com.coffemantang.With_Me_BACK.dto.MemberDTO;
import com.coffemantang.With_Me_BACK.model.Member;
import com.coffemantang.With_Me_BACK.persistence.MemberRepository;
import com.coffemantang.With_Me_BACK.persistence.PlanMembersRepository;
import com.coffemantang.With_Me_BACK.persistence.ReviewMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final ReviewMemberRepository reviewMemberRepository;

    private final PlanMembersRepository planMembersRepository;

    // 새 계정 생성 - 이메일 중복 검사
    public MemberDTO add(MemberDTO memberDTO){

        if(memberDTO == null || memberDTO.getEmail() == null){
            log.warn("MemberService.add() : memberEntity에 email이 없습니다.");
            throw new RuntimeException("MemberService.add() : memberEntity에 email이 없습니다.");
        }

        final String email = memberDTO.getEmail();
        if(memberRepository.existsByEmail(email)){
            log.warn("MemberService.add() : 해당 email이 이미 존재합니다.");
            throw new RuntimeException("MemberService.add() : 해당 email이 이미 존재합니다.");
        }
        // 닉네임 중복 체크
        boolean check = checkNickname(memberDTO.getNickname());
        if(!check){
            log.warn("MemberService.add() : 중복되는 닉네임입니다.");
            throw new RuntimeException("MemberService.add() : 중복되는 닉네임입니다.");
        }

        try {
            Member member = Member.builder()
                    .email(memberDTO.getEmail())
                    .password(passwordEncoder.encode(memberDTO.getPassword()))
                    .name(memberDTO.getName())
                    .nickname(memberDTO.getNickname())
                    .contact(memberDTO.getContact())
                    .birthday(memberDTO.getBirthday())
                    .joinDay(LocalDateTime.now()) // 현재 시간
                    .gender(memberDTO.getGender())
                    .address1(memberDTO.getAddress1())
                    .address2(memberDTO.getAddress2())
                    .question(memberDTO.getQuestion())
                    .answer(memberDTO.getAnswer()).build();

            int memberId = memberRepository.save(member).getMemberId();

            // 이미지가 있는 경우
            if (memberDTO.checkFileNull()) {

                MultipartFile multipartFile = memberDTO.getFile().get(0);
                String current_date = null;

                if (!multipartFile.isEmpty()) {
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    current_date = now.format(dateTimeFormatter);

                    //            String absolutePath = new File("").getAbsolutePath() + File.separator + File.separator;
                    String absolutePath = "C:" + File.separator + "withMeImgs" + File.separator + "member";

                    //            String path = "images" + File.separator + current_date;
                    String path = absolutePath;
                    File file = new File(path);

                    if (!file.exists()) {
                        boolean wasSuccessful = file.mkdirs();

                        if (!wasSuccessful) {
                            log.warn("file : was not successful");
                        }
                    }
                    while (true) {
                        String originalFileExtension;
                        String contentType = multipartFile.getContentType();

                        if (ObjectUtils.isEmpty(contentType)) {
                            break;
                        } else {
                            if (contentType.contains("image/jpeg")) {
                                originalFileExtension = ".jpg";
                            } else if (contentType.contains("images/png")) {
                                originalFileExtension = ".png";
                            } else {
                                break;
                            }
                        }

                        String new_file_name = String.valueOf(memberId);

                        member.setProfileImg(new_file_name + originalFileExtension);

                        memberRepository.save(member);

                        file = new File(absolutePath + File.separator + new_file_name + originalFileExtension);
                        multipartFile.transferTo(file);

                        file.setWritable(true);
                        file.setReadable(true);
                        break;
                    }
                }
            }

            MemberDTO responseMemberDTO = new MemberDTO(member);
            return responseMemberDTO;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("MemberService.add() : 에러 발생.");
        }

    }


    // 닉네임 중복 체크
    public boolean checkNickname(final String nickname){

        if(nickname == null || nickname.equals("")){
            log.warn("MemberService.checkNickname() : nickname 값이 이상해요");
            throw new RuntimeException("MemberService.checkNickname() : nickname 값이 이상해요");
        }

        int count = memberRepository.findByNickname(nickname);
        if(count > 0){
            return false;
        }
        return true;

    }

    // 로그인 - 자격증명
    public MemberDTO getByCredentials(final String email, final String password, final PasswordEncoder encoder){

        final Member originalMember = memberRepository.findByEmail(email); // 이메일로 MemberEntity를 찾음
        // 패스워드가 같은지 확인
        if(originalMember != null && encoder.matches(password, originalMember.getPassword())){
            MemberDTO memberDTO = new MemberDTO(originalMember);
            return memberDTO;
        }
        return null;

    }

    // 프로필 보기
    public MemberDTO viewProfile(MemberDTO memberDTO) {

        try {
            // MemberEntity 가져오기
            Member member = memberRepository.findByMemberId(memberDTO.getMemberId());
            // 평점 평균 가져오기
            double rating = reviewMemberRepository.selectAVGRatingByReviewed(member.getMemberId());
            // 여행 횟수 가져오기
            int travelRecord = planMembersRepository.selectCountByMemberIdAndCheckReviewGreaterThan(member.getMemberId(), 0);

            //MemberDTO 생성
            MemberDTO responseMemberDTO = new MemberDTO();
            responseMemberDTO.setMemberId(member.getMemberId());
            responseMemberDTO.setNickname(member.getNickname());
            responseMemberDTO.setGender(member.getGender());
            responseMemberDTO.setContact(member.getContact());
            responseMemberDTO.setRating(rating);
            responseMemberDTO.setTravelRecord(travelRecord);
            // 프로필사진 있다면 추가
            if (member.getProfileImg() != null) {
                responseMemberDTO.setProfileImg("http://localhost:8080/images/member/" + member.getProfileImg());
            }

            return responseMemberDTO;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("MemberService.viewProfile() : 에러 발생.");
        }
    }

    // 같은 이메일이 있는지 확인
    public Boolean checkEmail(final String email){
        if(email == null || email.equals("")){
            log.warn("MemberService.checkEmail() : email 값이 이상해요");
            throw new RuntimeException("MemberService.checkEmail() : email 값이 이상해요");
        }
        if(memberRepository.existsByEmail(email)){ //이메일이 이미 있으면 false리턴
            return false;
        }
        return true;
    }


    // 아이디로 멤버의 비밀번호 찾기 질문 가져오기
    public String getQuestion(final int memberId){
        if(memberId <= 0){
            log.warn("MemberService.getQuestion() : memberId 값이 이상해요");
            throw new RuntimeException("MemberService.getQuestion() : memberId 값이 이상해요");
        }
        final String question = memberRepository.findQuestionByMemberId(memberId); // 아이디로 Question 찾기
        return question;
    }


    // 들어온 본인확인답변과 아이디가 일치하는지 체크 후 비밀번호 변경하고 string 리턴
    @Transactional
    public String checkAnswer(final int memberId, final String answer, PasswordEncoder passwordEncoder){
        if(memberId <= 0 || answer == null){
            log.warn("MemberService.checkAnswer() : 들어온 값이 이상해요");
            throw new RuntimeException("MemberService.checkAnswer() : 들어온 값이 이상해요");
        }
        final int findCount = memberRepository.findByAnswer(memberId, answer); //
        if(findCount < 1){
            // 답변이 일치하지 않으면
            log.warn("MemberService.checkAnswer() : 답변이 달라요");
            throw new RuntimeException("MemberService.checkAnswer() : 답변이 달라요");
        }
        // 답변이 일치하면 비밀번호 랜덤하게 변경 후 변경된 비밀번호 리턴
        final String pw = changeMemberPw(memberId, passwordEncoder);
        return pw;
    }

    // 멤버의 비밀번호 랜덤하게 변경하고 entity 리턴
    @Transactional
    public String changeMemberPw(final int memberId, PasswordEncoder passwordEncoder){
        if(memberId <= 0){
            log.warn("MemberService.changeMemberPw() : memberId 값이 이상해요");
            throw new RuntimeException("MemberService.changeMemberPw() : memberId 값이 이상해요");
        }
        final Member member = memberRepository.findByMemberId(memberId); // 아이디로 MemberEntity 찾음
        // 12자리 랜덤 비밀번호 생성
        final String pw = RandomStringUtils.random(12, "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        member.changePassword(passwordEncoder.encode(pw)); // 변경
        memberRepository.save(member);
        return pw;
    }

    // 현재 비밀번호와 변경할 비밀번호 받아서 비밀번호 변경
    @Transactional
    public boolean changePw(final int memberId, final String curPw, final String chgPw, PasswordEncoder passwordEncoder){
        if(curPw == null || curPw.equals("") || chgPw == null | chgPw.equals("")){
            log.warn("MemberService.changePw() : 들어온 값이 이상해요");
            throw new RuntimeException("MemberService.changePw() : 들어온 값이 이상해요");
        }
        if(memberId < 1){
            log.warn("MemberService.changePw() : memberId 값이 이상해요");
            throw new RuntimeException("MemberService.changePw() : memberId 값이 이상해요");
        }
        // 현재 비밀번호가 맞는지 검사
        String originPassword = memberRepository.findPasswordByMemberId(memberId); //DB에 들어가있는 PW
        if(!passwordEncoder.matches(curPw, originPassword)){
            //비밀번호가 다르면
            log.warn("MemberService.changePw() : 비밀번호가 달라요");
            throw new RuntimeException("MemberService.changePw() : 비밀번호가 달라요");
        }
        //비밀번호가 맞으면 비밀번호 변경
        final Member member = memberRepository.findByMemberId(memberId);
        member.changePassword(passwordEncoder.encode(chgPw));
        memberRepository.save(member);
        return true;
    }

    // 닉네임 수정하기 - 닉네임 리턴
    @Transactional
    public String updateNickname(final int memberId, final String chgNickname){
        if(memberId < 1){
            log.warn("MemberService.updateNickname() : memberId 값이 이상해요");
            throw new RuntimeException("MemberService.updateNickname() : memberId 값이 이상해요");
        }
        if(chgNickname == null || chgNickname.equals("")){
            log.warn("MemberService.updateNickname() : chgNickname 값이 이상해요");
            throw new RuntimeException("MemberService.updateNickname() : chgNickname 값이 이상해요");
        }
        // 닉네임 중복 체크
        boolean check = checkNickname(chgNickname);
        if(!check){
            log.warn("duplicated nickname");
            throw new RuntimeException("duplicated nickname");
        }

        final Member memberEntity = memberRepository.findByMemberId(memberId);
        memberEntity.setNickname(chgNickname);
        memberRepository.save(memberEntity); // 값 변경
        final String nickname = memberRepository.findNicknameByMemberId(memberId); //아이디로 현재 닉네임 가져옴

        return nickname;
    }

    // 이름 변경
    @Transactional
    public String updateName(final int memberId, final String chgName){
        if(memberId < 1){
            log.warn("MemberService.updateName() : memberId 값이 이상해요");
            throw new RuntimeException("MemberService.updateName() : memberId 값이 이상해요");
        }
        if(chgName == null || chgName.equals("")){
            log.warn("MemberService.updatename() : name 값이 이상해요");
            throw new RuntimeException("MemberService.updatename() : name 값이 이상해요");
        }
        final Member memberEntity = memberRepository.findByMemberId(memberId);
        memberEntity.setName(chgName);
        memberRepository.save(memberEntity);
        String name = memberRepository.findNameByMemberId(memberId);
        return name;
    }

    // 본인확인 질문답변 변경
    @Transactional
    public Member updateQA(final int memberId, final String chgQuestion, final String chgAnswer){
        if(memberId < 1){
            log.warn("MemberService.updateQA() : memberId 값이 이상해요");
            throw new RuntimeException("MemberService.updateQA() : memberId 값이 이상해요");
        }
        if(chgQuestion == null || chgQuestion.equals("")){
            log.warn("MemberService.updateQA() : chgQuestion 값이 이상해요");
            throw new RuntimeException("MemberService.updateQA() : chgQuestion 값이 이상해요");
        }
        if(chgAnswer == null || chgAnswer.equals("")){
            log.warn("MemberService.updateQA() : chgAnswer 값이 이상해요");
            throw new RuntimeException("MemberService.updateQA() : chgAnswer 값이 이상해요");
        }
        final Member memberEntity = memberRepository.findByMemberId(memberId);
        memberEntity.setQuestion(chgQuestion);
        memberEntity.setAnswer(chgAnswer);
        memberRepository.save(memberEntity);
        final Member chgMemberEntity = memberRepository.findQuestionAnswerByMemberId(memberId);
        return chgMemberEntity;
    }
}
