package com.coffemantang.With_Me_BACK.persistence;

import com.coffemantang.With_Me_BACK.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Integer> {

    // 해당하는 이메일이 있는지 확인
    Boolean existsByEmail (String email);

    // 같은 닉네임이 있는지 확인
    @Query(value = "SELECT COUNT(member_id) FROM member WHERE nickname = :nickname", nativeQuery = true)
    int findByNickname(@Param("nickname") String nickname);

    // 이메일로 엔티티 가져오기
    Member findByEmail(String email);

    // memberId로 nickname 가져오기
    @Query(value = "SELECT nickname FROM member WHERE member_id = :memberId", nativeQuery = true)
    String findNicknameByMemberId(@Param("memberId") int memberId);

    // memberId로 엔티티 가져오기
    Member findByMemberId(int memberId);
}
