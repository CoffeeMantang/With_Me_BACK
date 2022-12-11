package com.coffemantang.With_Me_BACK.service;

import com.coffemantang.With_Me_BACK.dto.MemberDTO;
import com.coffemantang.With_Me_BACK.model.Member;
import com.coffemantang.With_Me_BACK.model.ReviewMember;
import com.coffemantang.With_Me_BACK.persistence.MemberRepository;
import com.coffemantang.With_Me_BACK.persistence.PlanMembersRepository;
import com.coffemantang.With_Me_BACK.persistence.ReviewMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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
    public MemberDTO viewProfile(int memberId, MemberDTO memberDTO) {

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
}
