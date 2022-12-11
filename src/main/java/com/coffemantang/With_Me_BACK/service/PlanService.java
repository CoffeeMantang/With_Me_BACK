package com.coffemantang.With_Me_BACK.service;

import com.coffemantang.With_Me_BACK.dto.CheckDTO;
import com.coffemantang.With_Me_BACK.dto.PlanDTO;
import com.coffemantang.With_Me_BACK.model.Plan;
import com.coffemantang.With_Me_BACK.persistence.PlanMembersRepository;
import com.coffemantang.With_Me_BACK.persistence.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.hibernate.annotations.Check;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;

    private final PlanMembersRepository planMembersRepository;

    // 조건 체크
    public void checkCondition(int memberId, LocalDateTime startDate, LocalDateTime endDate) {

        try {

            // 유저가 작성하지 않은 평가가 있다면
            int chkReview = planMembersRepository.selectCountByMemberIdAndCheck(memberId, 1);
            if (chkReview > 0) {
                log.warn("PlanService.checkCondition() : 구성원 평가를 작성하지 않았습니다.");
                throw new RuntimeException("PlanService.checkCondition() : 구성원 평가를 작성하지 않았습니다.");
            }

            // 유저가 만들거나 참여하려는 여행의 일정이 전에 참여한 일정과 겹친다면
            int chkDate = planRepository.selectCountByMemberIdAndStateAndDate(memberId, 3, endDate, startDate);
            if (chkDate > 0) {
                log.warn("PlanService.checkCondition() : 이전에 참여했던 일정과 기간이 겹칩니다.");
                throw new RuntimeException("PlanService.checkCondition() : 이전에 참여했던 일정과 기간이 겹칩니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PlanService.checkCondition() : 에러 발생.");
        }

    }


    // 여행 일정 추가
    public PlanDTO addPlan(int memberId, PlanDTO planDTO) {

        // 여행은 종료되었는데 check가 0이면 1로 변경
        planMembersRepository.updateCheck1(memberId);

        if(memberId != planDTO.getMemberId()) {
            log.warn("PlanService.addPlan() : 로그인된 유저와 작성자가 다릅니다.");
            throw new RuntimeException("PlanService.addPlan() : 로그인된 유저와 작성자가 다릅니다.");
        }
        // 조건 체크
        checkCondition(memberId, planDTO.getStartDate(), planDTO.getEndDate());

        try {

            // 엔티티에 저장
            Plan plan = new Plan();
            plan.setMemberId(planDTO.getMemberId());
            plan.setTitle(planDTO.getTitle());
            plan.setState(0);
            plan.setPersonnel(planDTO.getPersonnel());
            plan.setPostDate(planDTO.getPostDate());
            plan.setDeadline(planDTO.getDeadline());
            plan.setStartDate(planDTO.getStartDate());
            plan.setEndDate(planDTO.getEndDate());
            plan.setNotice(planDTO.getNotice());
            plan.setTheme(planDTO.getTheme());
            plan.setHit(0);

            planRepository.save(plan);
            // 저장한 엔티티 DTO에 담아서 리턴
            PlanDTO responsePlanDTO = new PlanDTO(plan);

            return responsePlanDTO;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PlanService.addPlan() : 에러 발생.");
        }

    }

    // state 변환
    public CheckDTO checkState(CheckDTO checkDTO) {

        List<PlanDTO> planDTOList = checkDTO.getPlanDTOList();
        PlanDTO planDTO = checkDTO.getPlanDTO();
        int planId = checkDTO.getPlanId();
        CheckDTO responseCheckDTO = new CheckDTO();

        // 일정 한 개
        if (planDTO != null || planId != 0) {
            if (planDTO != null) planId = planDTO.getPlanId();

            planRepository.updateState(LocalDateTime.now() + "", planId);
            Plan plan = planRepository.findById(planId);
            responseCheckDTO.setPlanDTO(new PlanDTO(plan));

        }
        // 일정 여러개
        else if(planDTOList != null){

            List<PlanDTO> tempPlanDTOList = new ArrayList<>();
            for (PlanDTO tempPlanDTO : planDTOList) {

                planRepository.updateState(LocalDateTime.now() + "", tempPlanDTO.getPlanId());
                Plan plan = planRepository.findById(tempPlanDTO.getPlanId());
                tempPlanDTOList.add(new PlanDTO(plan));

            }
            responseCheckDTO.setPlanDTOList(tempPlanDTOList);

        }

        return responseCheckDTO;

    }



}
