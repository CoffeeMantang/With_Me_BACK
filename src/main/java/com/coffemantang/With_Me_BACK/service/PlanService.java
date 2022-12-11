package com.coffemantang.With_Me_BACK.service;

import com.coffemantang.With_Me_BACK.dto.CheckDTO;
import com.coffemantang.With_Me_BACK.dto.PlanDTO;
import com.coffemantang.With_Me_BACK.dto.PlanDetailDTO;
import com.coffemantang.With_Me_BACK.model.Plan;
import com.coffemantang.With_Me_BACK.model.PlanDetail;
import com.coffemantang.With_Me_BACK.model.PlanMembers;
import com.coffemantang.With_Me_BACK.persistence.PlanDetailRepository;
import com.coffemantang.With_Me_BACK.persistence.PlanMembersRepository;
import com.coffemantang.With_Me_BACK.persistence.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;

    private final PlanDetailRepository planDetailRepository;

    private final PlanMembersRepository planMembersRepository;

    private final PlanDetailService planDetailService;

    // 조건 체크
    public void checkCondition(int memberId, LocalDateTime startDate, LocalDateTime endDate) {

        try {

            // 유저가 작성하지 않은 평가가 있다면
            int chkReview = planMembersRepository.selectCountByMemberIdAndCheckReview(memberId, 1);
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
            int planId = planRepository.save(plan).getPlanId();

            // planmembers에 추가
            PlanMembers planMembers = new PlanMembers();
            planMembers.setPlanId(planId);
            planMembers.setMemberId(planDTO.getMemberId());
            planMembers.setCheckReview(0);
            planMembersRepository.save(planMembers);

            // 저장한 엔티티 DTO에 담아서 리턴
            PlanDTO responsePlanDTO = new PlanDTO(plan);

            return responsePlanDTO;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PlanService.addPlan() : 에러 발생.");
        }

    }

    // 여행 일정 수정
    public PlanDTO updatePlan(int memberId, PlanDTO planDTO) {

        if(memberId != planDTO.getMemberId()) {
            log.warn("PlanService.updatePlan() : 로그인된 유저와 작성자가 다릅니다.");
            throw new RuntimeException("PlanService.updatePlan() : 로그인된 유저와 작성자가 다릅니다.");
        }
        // 조건 체크
        if(planDTO.getState() > 1) {
            log.warn("PlanService.updatePlan() : 여행이 시작된 후엔 수정할 수 없습니다.");
            throw new RuntimeException("PlanService.updatePlan() : 여행이 시작된 후엔 수정할 수 없습니다.");
        }
        checkCondition(memberId, planDTO.getStartDate(), planDTO.getEndDate());

        try {
            System.out.println(planDTO);
            // 엔티티에 저장
            Plan plan = planRepository.findById(planDTO.getPlanId());
            plan.setMemberId(planDTO.getMemberId());
            plan.setTitle(planDTO.getTitle());
            plan.setPersonnel(planDTO.getPersonnel());
            plan.setPostDate(planDTO.getPostDate());
            plan.setDeadline(planDTO.getDeadline());
            plan.setStartDate(planDTO.getStartDate());
            plan.setEndDate(planDTO.getEndDate());
            plan.setNotice(planDTO.getNotice());
            plan.setTheme(planDTO.getTheme());
            planRepository.save(plan);

            // 저장한 엔티티 DTO에 담아서 리턴
            PlanDTO responsePlanDTO = new PlanDTO(plan);

            return responsePlanDTO;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PlanService.updatePlan() : 에러 발생.");
        }

    }

    // 여행 일정 삭제
    public void deletePlan(int memberId, PlanDTO planDTO) {

        if(memberId != planDTO.getMemberId()) {
            log.warn("PlanService.deletePlan() : 로그인된 유저와 작성자가 다릅니다.");
            throw new RuntimeException("PlanService.deletePlan() : 로그인된 유저와 작성자가 다릅니다.");
        }

        try {

            Plan plan = planRepository.findById(planDTO.getPlanId());
            planDetailService.deleteDetail(planDTO.getPlanId());
            planRepository.delete(plan);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PlanService.deletePlan() : 에러 발생.");
        }
    }

    // 여행 일정 보기
    public PlanDTO viewPlan(PlanDTO planDTO) {

        checkState(new CheckDTO(planDTO));

        try {

            // 여행 엔티티 가져오기
            Plan plan = planRepository.findById(planDTO.getPlanId());
            // 여행 디테일 리스트 가져와서 이미지 설정
            List<PlanDetail> planDetailList = planDetailRepository.findByPlanId(planDTO.getPlanId());
            List<PlanDetailDTO> planDetailDTOList = new ArrayList<>();
            for (PlanDetail planDetail : planDetailList) {
                planDetail.setDetailImg("http://localhost:8080/withMeImgs/planDetail/" + planDetail.getDetailImg());
                PlanDetailDTO planDetailDTO = new PlanDetailDTO(planDetail);
                planDetailDTOList.add(planDetailDTO);
            }

            // planDTO 생성 후 할당
            PlanDTO responsePlanDTO = new PlanDTO(plan);
            responsePlanDTO.setPlanDetailDTOList(planDetailDTOList);

            return responsePlanDTO;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PlanService.viewPlan() : 에러 발생.");
        }

    }



    // 여행 일정 리스트
//    public List<PlanDTO> listPlan(Pageable pageable) {
//
//        try {
//
//            Page<Plan> planPage = planRepository.findAllOrderByPlanIdDESC(pageable);
//            List<Plan> planList = planPage.getContent();
//            List<PlanDTO> planDTOList = new ArrayList<>();
//
//            for (Plan plan : planList) {
//                PlanDTO planDTO = new PlanDTO(plan);
//                planDTOList.add(planDTO);
//            }
//            return planDTOList;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("PlanService.listPlan() : 에러 발생.");
//        }
//    }
}
