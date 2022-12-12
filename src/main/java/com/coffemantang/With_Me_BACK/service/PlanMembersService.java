package com.coffemantang.With_Me_BACK.service;

import com.coffemantang.With_Me_BACK.dto.PlanDTO;
import com.coffemantang.With_Me_BACK.dto.PlanMembersDTO;
import com.coffemantang.With_Me_BACK.model.ApplyPlan;
import com.coffemantang.With_Me_BACK.model.PlanMembers;
import com.coffemantang.With_Me_BACK.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanMembersService {

    private final PlanMembersRepository planMembersRepository;

    private final PlanRepository planRepository;

    private final ApplyPlanRepository applyPlanRepository;

    private final MemberRepository memberRepository;

    private final ReviewMemberRepository reviewMemberRepository;

    // 여행 구성원 리스트
    public List<PlanMembersDTO> listPlanMembers(int memberId, PlanDTO planDTO) {

        long chk = planMembersRepository.countByPlanIdAndMemberId(planDTO.getPlanId(), memberId);
        if(chk == 0) {
            log.warn("PlanMembersService.viewPlanMembers() : 로그인된 유저는 구성원에 없습니다.");
            throw new RuntimeException("PlanMembersService.viewPlanMembers() : 로그인된 유저는 구성원에 없습니다.");
        }

        try {

            List<PlanMembers> planMembersList = planMembersRepository.findByPlanId(planDTO.getPlanId());
            List<PlanMembersDTO> planMembersDTOList = new ArrayList<>();
            for (PlanMembers planMembers : planMembersList) {
                PlanMembersDTO planMembersDTO = PlanMembersDTO.builder()
                        .planId(planMembers.getPlanId())
                        .planMembersId(planMembers.getPlanMembersId())
                        .build();
                planMembersDTO.setNickname(memberRepository.findNicknameByMemberId(planMembers.getMemberId()));
                planMembersDTOList.add(planMembersDTO);
            }

            return planMembersDTOList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PlanMembersService.viewPlanMembers() : 에러 발생");
        }

    }

    // 평가해야할 여행 구성원 리스트
    public List<PlanMembersDTO> listReviewPlanMembers(int memberId, PlanDTO planDTO) {

        long chk = planMembersRepository.countByPlanIdAndMemberId(planDTO.getPlanId(), memberId);
        if(chk == 0) {
            log.warn("PlanMembersService.viewPlanMembers() : 로그인된 유저는 구성원에 없습니다.");
            throw new RuntimeException("PlanMembersService.viewPlanMembers() : 로그인된 유저는 구성원에 없습니다.");
        }

        try {

            List<PlanMembers> planMembersList = planMembersRepository.findByPlanIdNotMemberId(planDTO.getPlanId(), memberId);
            List<PlanMembersDTO> planMembersDTOList = new ArrayList<>();
            for (PlanMembers planMembers : planMembersList) {
                PlanMembersDTO planMembersDTO = PlanMembersDTO.builder()
                            .planId(planMembers.getPlanId()).build();
                planMembersDTO.setNickname(memberRepository.findNicknameByMemberId(planMembers.getMemberId()));
                if (0 < reviewMemberRepository.countByPlanIdAndReviewerAndReviewed(planMembers.getPlanId(), memberId, planMembers.getMemberId()))
                    planMembersDTO.setPossibleReviewMember(1); // 작성함
                else planMembersDTO.setPossibleReviewMember(0); // 작성 안 함
                planMembersDTOList.add(planMembersDTO);
            }

            return planMembersDTOList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PlanMembersService.viewPlanMembers() : 에러 발생");
        }

    }

    // 구성원 내보내기
    public void outPlanMembers(int memberId, PlanMembersDTO planMembersDTO) {

        int tempMemberId = planRepository.selectMemberIdById(planMembersDTO.getPlanId());
        if(memberId != tempMemberId) {
            log.warn("PlanMembersService.outPlanMembers() : 로그인된 유저와 모집자가 다릅니다.");
            throw new RuntimeException("PlanMembersService.outPlanMembers() : 로그인된 유저와 모집자가 다릅니다.");
        }

        try {
            System.out.println(planMembersDTO);
            // 구성원 리스트에서 삭제
            PlanMembers planMembers = planMembersRepository.findById(planMembersDTO.getPlanMembersId());
            System.out.println(planMembers);
            planMembersRepository.delete(planMembers);

            // 여행 신청 상태 거절로 변경
            ApplyPlan applyPlan = applyPlanRepository.findByMemberIdAndPlanIdAndState(planMembersDTO.getMemberId(), planMembersDTO.getPlanId(), 1);
            applyPlan.setState(2);
            applyPlanRepository.save(applyPlan);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PlanMembersService.outPlanMembers() : 에러 발생");
        }

    }

}
