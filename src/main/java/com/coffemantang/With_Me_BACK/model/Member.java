package com.coffemantang.With_Me_BACK.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private int memberId;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "contact")
    private String contact; // 연락처 (핸드폰번호, 카카오톡 아이디 등)

    @Column(name = "gender")
    private int gender; // 0 : 남자 / 1 : 여자

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "joinday")
    private LocalDateTime joinDay;

    @Column(name = "question")
    private String question;

    @Column(name = "answer")
    private String answer;

    @Column(name = "address1")
    private String address1;

    @Column(name = "address2")
    private String address2;

    @Column(name = "profile_img")
    private String profileImg;

    public void changePassword(String password){
        this.password = password;
    }
}
