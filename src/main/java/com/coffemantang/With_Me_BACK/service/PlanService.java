package com.coffemantang.With_Me_BACK.service;

import com.coffemantang.With_Me_BACK.dto.PlanDTO;
import com.coffemantang.With_Me_BACK.persistence.PlanMembersRepository;
import com.coffemantang.With_Me_BACK.persistence.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;

    private final PlanMembersRepository planMembersRepository;

    // 조건 체크
    public int checkCondition(int memberId, PlanDTO planDTO) {

        if(memberId != planDTO.getMemberId()) {
            log.warn("PlanService.checkCondition() : 로그인된 유저와 체크 대상자가 다릅니다.");
            throw new RuntimeException("PlanService.checkCondition() : 로그인된 유저와 체크 대상자가 다릅니다.");
        }

        try {

            // 유저가 작성하지 않은 평가가 있다면 1 리턴
            int chkReview = planMembersRepository.selectCountByMemberIdAndCheck(planDTO.getMemberId(), 1);
            if (chkReview > 0) {
                log.warn("PlanService.checkCondition() : 구성원 평가를 작성하지 않았습니다.");
                return 1;
            }

            // 유저가 만들거나 참여하려는 여행의 일정이 전에 참여한 일정과 겹친다면 2 리턴
            int chkDate = planRepository.selectCountByMemberIdAndStateAndDate(planDTO.getMemberId(), 3, planDTO.getEndDate(), planDTO.getStartDate());
            if (chkDate > 0) {
                log.warn("PlanService.checkCondition() : 전에 참여했던 일정과 겹칩니다.");
                return 2;
            }

            return 0;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PlanService.checkCondition() : 에러 발생.");
        }

    }


}
