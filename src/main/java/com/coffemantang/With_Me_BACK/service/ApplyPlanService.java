package com.coffemantang.With_Me_BACK.service;

import com.coffemantang.With_Me_BACK.dto.ApplyPlanDTO;
import com.coffemantang.With_Me_BACK.dto.CheckDTO;
import com.coffemantang.With_Me_BACK.model.ApplyPlan;
import com.coffemantang.With_Me_BACK.model.PlanMembers;
import com.coffemantang.With_Me_BACK.persistence.ApplyPlanRepository;
import com.coffemantang.With_Me_BACK.persistence.MemberRepository;
import com.coffemantang.With_Me_BACK.persistence.PlanMembersRepository;
import com.coffemantang.With_Me_BACK.persistence.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplyPlanService {

    private final ApplyPlanRepository applyPlanRepository;

    private final PlanMembersRepository planMembersRepository;

    private final PlanRepository planRepository;

    private final PlanService planService;

    private final MemberRepository memberRepository;

    // 여행 참여 신청
    public void applyPlan(int memberId, ApplyPlanDTO applyPlanDTO) {

        try {
            log.warn("들어온 planId는 " + applyPlanDTO.getPlanId());
            log.warn("들어온 memberId는 " + memberId);
            // 신청 중복 검사
            long chk = planMembersRepository.countByPlanIdAndMemberId(applyPlanDTO.getPlanId(), memberId);
            Long chk2 = applyPlanRepository.countByPlanIdAndMemberIdAndState(applyPlanDTO.getPlanId(), memberId, 0);
            log.warn("검사결과: " + chk);
            if (chk > 0) {
                log.warn("ApplyPlanService.applyPlan() : 이미 신청한 여행 일정입니다.");
                throw new RuntimeException("ApplyPlanService.applyPlan() : 이미 신청한 여행 일정입니다.");
            }
            if (chk2 > 0 || chk2 == null) {
                log.warn("ApplyPlanService.applyPlan() : 이미 신청한 여행 일정입니다.");
                throw new RuntimeException("ApplyPlanService.applyPlan() : 이미 신청한 여행 일정입니다.");
            }
            // 일정 상태 검사
            planService.checkState(new CheckDTO(applyPlanDTO.getPlanId()));
            // 조건 체크
            planService.checkCondition(memberId, applyPlanDTO.getStartDate(), applyPlanDTO.getEndDate());

            ApplyPlan applyPlan = new ApplyPlan();
            applyPlan.setMemberId(memberId);
            applyPlan.setPlanId(applyPlanDTO.getPlanId());
            applyPlan.setContent(applyPlanDTO.getContent());
            applyPlan.setState(0);
            applyPlanRepository.save(applyPlan);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ApplyPlanService.applyPlan() : 에러 발생");
        }

    }

    // 신청 수락/거절
    public void acceptRefuse(int memberId, ApplyPlanDTO applyPlanDTO, int isAccept) {

        if(memberId != planRepository.selectMemberIdById(applyPlanDTO.getPlanId())) {
            log.warn("ApplyPlanService.acceptRefuse() : 로그인된 유저와 여행 모집자가 다릅니다.");
            throw new RuntimeException("ApplyPlanService.acceptRefuse() : 로그인된 유저와 여행 모집자가 다릅니다.");
        }

        try {
            ApplyPlan applyPlan = applyPlanRepository.findByApplyPlanId(applyPlanDTO.getApplyPlanId());

            switch (isAccept) {
                case 0 :
                    applyPlan.setState(1);
                    PlanMembers planMembers = new PlanMembers();
                    planMembers.setPlanId(applyPlan.getPlanId());
                    planMembers.setMemberId(applyPlan.getMemberId());
                    planMembers.setCheckReview(0);
                    planMembersRepository.save(planMembers);
                    break;
                case 1 :
                    applyPlan.setState(2);
                    break;
            }

            applyPlanRepository.save(applyPlan);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ApplyPlanService.acceptRefuse() : 에러 발생");
        }

    }

    // 신청 취소
    public void cancelApply(int memberId, ApplyPlanDTO applyPlanDTO) {

        try {

            ApplyPlan applyPlan = applyPlanRepository.findByMemberIdAndPlanIdAndState(memberId, applyPlanDTO.getPlanId(), 0);
            applyPlan.setState(3);
            applyPlanRepository.save(applyPlan);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ApplyPlanService.cancelApply() : 에러 발생");
        }

    }

    // 받은 신청 리스트
    public List<ApplyPlanDTO> receivedList(int memberId) {

        try {

            List<ApplyPlan> applyPlanList = applyPlanRepository.selectApplyListByMemberIdOrderByApplyPlanIdDesc(memberId, 0);
            List<ApplyPlanDTO> applyPlanDTOList = new ArrayList<>();
            for (ApplyPlan applyPlan : applyPlanList) {
                ApplyPlanDTO applyPlanDTO = ApplyPlanDTO.builder()
                        .planId(applyPlan.getPlanId())
                        .content(applyPlan.getContent())
                        .applyPlanId(applyPlan.getApplyPlanId())
                        .nickname(memberRepository.findNicknameByMemberId(applyPlan.getMemberId()))
                        .build();
                applyPlanDTOList.add(applyPlanDTO);
            }

            return applyPlanDTOList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ApplyPlanService.receivedList() : 에러 발생");
        }
    }

    // 보낸 신청 리스트
    public List<ApplyPlanDTO> responseList(int memberId) {

        try {

            List<ApplyPlan> applyPlanList = applyPlanRepository.findByMemberIdAndStateLessThanOrderByApplyPlanIdDesc(memberId, 3);
            List<ApplyPlanDTO> applyPlanDTOList = new ArrayList<>();
            for (ApplyPlan applyPlan : applyPlanList) {
                ApplyPlanDTO applyPlanDTO = ApplyPlanDTO.builder()
                        .planId(applyPlan.getPlanId())
                        .content(applyPlan.getContent())
                        .applyPlanId(applyPlan.getApplyPlanId())
                        .state(applyPlan.getState())
                        .nickname(memberRepository.findNicknameByMemberId(applyPlan.getMemberId()))
                        .build();
                applyPlanDTOList.add(applyPlanDTO);
            }

            return applyPlanDTOList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ApplyPlanService.responseList() : 에러 발생");
        }
    }
}
