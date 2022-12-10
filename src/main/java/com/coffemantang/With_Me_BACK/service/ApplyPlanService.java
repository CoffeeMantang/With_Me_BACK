package com.coffemantang.With_Me_BACK.service;

import com.coffemantang.With_Me_BACK.dto.ApplyPlanDTO;
import com.coffemantang.With_Me_BACK.model.ApplyPlan;
import com.coffemantang.With_Me_BACK.model.PlanMembers;
import com.coffemantang.With_Me_BACK.persistence.ApplyPlanRepository;
import com.coffemantang.With_Me_BACK.persistence.PlanMembersRepository;
import com.coffemantang.With_Me_BACK.persistence.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplyPlanService {

    private final ApplyPlanRepository applyPlanRepository;

    private final PlanMembersRepository planMembersRepository;

    private final PlanRepository planRepository;

    // 여행 참여 신청
    public void applyPlan(int memberId, ApplyPlanDTO applyPlanDTO) {

        if(memberId != applyPlanDTO.getMemberId()) {
            log.warn("ApplyPlanService.applyPlan() : 로그인된 유저와 신청자가 다릅니다.");
            throw new RuntimeException("ApplyPlanService.applyPlan() : 로그인된 유저와 신청자가 다릅니다.");
        }

        try {

            ApplyPlan applyPlan = new ApplyPlan();
            applyPlan.setMemberId(applyPlanDTO.getMemberId());
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

        if(memberId != applyPlanDTO.getMemberId()) {
            log.warn("ApplyPlanService.cancelApply() : 로그인된 유저와 신청자가 다릅니다.");
            throw new RuntimeException("ApplyPlanService.cancelApply() : 로그인된 유저와 신청자가 다릅니다.");
        }

        try {

            ApplyPlan applyPlan = applyPlanRepository.findByApplyPlanId(applyPlanDTO.getApplyPlanId());
            applyPlan.setState(3);
            applyPlanRepository.save(applyPlan);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ApplyPlanService.cancelApply() : 에러 발생");
        }

    }
}
