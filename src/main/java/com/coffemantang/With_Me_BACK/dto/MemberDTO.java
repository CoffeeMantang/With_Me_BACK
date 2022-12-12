package com.coffemantang.With_Me_BACK.dto;

import com.coffemantang.With_Me_BACK.model.Member;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {

    private String token;

    private int memberId;

    private String password;

    private String email;

    private String name;

    private String nickname;

    private String contact;

    private int gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate birthday;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime joinDay;

    private String question;

    private String answer;

    private String address1;

    private String address2;

    private String profileImg;

    private List<MultipartFile> file;

    private double rating; // 평점

    private int travelRecord; // 여행 횟수

    public MemberDTO(final Member member) {

        this.memberId = member.getMemberId();
        this.password = member.getPassword();
        this.email = member.getEmail();
        this.name = member.getName();
        this.nickname = member.getNickname();
        this.contact = member.getContact();
        this.gender = member.getGender();
        this.birthday = member.getBirthday();
        this.joinDay = member.getJoinDay();
        this.question = member.getQuestion();
        this.answer = member.getAnswer();
        this.address1 = member.getAddress1();
        this.address2 = member.getAddress2();
        this.profileImg = member.getProfileImg();

    }

    // 파일 null 체크
    public boolean checkFileNull() {
        return this.file != null;
    }
}
