package com.coffemantang.With_Me_BACK.service;

import com.coffemantang.With_Me_BACK.dto.CheckDTO;
import com.coffemantang.With_Me_BACK.dto.PlanDTO;
import com.coffemantang.With_Me_BACK.dto.PlanDetailDTO;
import com.coffemantang.With_Me_BACK.model.Member;
import com.coffemantang.With_Me_BACK.model.Plan;
import com.coffemantang.With_Me_BACK.model.PlanDetail;
import com.coffemantang.With_Me_BACK.model.PlanMembers;
import com.coffemantang.With_Me_BACK.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
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
    private final MemberRepository memberRepository;

    private final ReviewPlanRepository reviewPlanRepository;

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
    @Transactional
    public PlanDTO addPlan(int memberId, PlanDTO planDTO) {

        try {

            // 여행은 종료되었는데 check가 0이면 1로 변경
            planMembersRepository.updateCheck1(memberId);

            // 조건 체크
            checkCondition(memberId, planDTO.getStartDate(), planDTO.getEndDate());

            // 엔티티에 저장
            Plan plan = new Plan();
            plan.setMemberId(memberId);
            plan.setTitle(planDTO.getTitle());
            plan.setState(0);
            plan.setPersonnel(planDTO.getPersonnel());
            plan.setPostDate(LocalDateTime.now());
            plan.setDeadline(planDTO.getDeadline());
            plan.setStartDate(planDTO.getStartDate());
            plan.setEndDate(planDTO.getEndDate());
            plan.setNotice(planDTO.getNotice());
            plan.setPlace((planDTO.getPlace()));
            plan.setTheme(planDTO.getTheme());
            plan.setHit(0);
            plan.setPlace(planDTO.getPlace());

            int planId = planRepository.save(plan).getPlanId();

            // 저장한 엔티티 DTO에 담아서 리턴
            PlanDTO responsePlanDTO = PlanDTO.builder()
                    .planId(planId)
                    .title(plan.getTitle())
                    .personnel(plan.getPersonnel())
                    .postDate(plan.getPostDate())
                    .deadline(plan.getDeadline())
                    .startDate(plan.getStartDate())
                    .endDate(plan.getEndDate())
                    .notice(plan.getNotice())
                    .theme(plan.getTheme())
                    .place(plan.getPlace())
                    .build();
            // PlanDetail 추가
            if (planDTO.getPlanDetailDTOList() != null) {

                List<PlanDetailDTO> planDetailDTOList = new ArrayList<>();
                for (PlanDetailDTO planDetailDTO : planDTO.getPlanDetailDTOList()) {

                    // 엔티티 빌더
                    PlanDetail planDetail = PlanDetail.builder()
                            .planId(planId)
                            .detailDate(planDetailDTO.getDetailDate())
                            .content(planDetailDTO.getContent())
                            .build();

                    int planDetailId = planDetailRepository.save(planDetail).getPlanDetailId();

                    // 이미지가 있는 경우
                    if (planDetailDTO.checkFileNull()) {

                        MultipartFile multipartFile = planDetailDTO.getFile().get(0);
                        String current_date = null;

                        if (!multipartFile.isEmpty()) {
                            LocalDateTime now = LocalDateTime.now();
                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                            current_date = now.format(dateTimeFormatter);

                            String absolutePath = "C:" + File.separator + "withMeImgs" + File.separator + "planDetail";

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

                                planDetail.setDetailImg(new_file_name + originalFileExtension);

                                planDetailRepository.save(planDetail);

                                file = new File(absolutePath + File.separator + new_file_name + originalFileExtension);
                                multipartFile.transferTo(file);

                                file.setWritable(true);
                                file.setReadable(true);
                                break;
                            }
                        }
                    } // 이미지처리 끝

                    PlanDetailDTO tempDTO = new PlanDetailDTO(planDetail);
                    planDetailDTOList.add(tempDTO);

                } // for문 끝

                // responsedto에 담기
                responsePlanDTO.setPlanDetailDTOList(planDetailDTOList);

            } // detailList null 체크 끝

            // planmembers에 추가
            PlanMembers planMembers = new PlanMembers();
            planMembers.setPlanId(planId);
            planMembers.setMemberId(memberId);
            planMembers.setCheckReview(0);
            planMembersRepository.save(planMembers);

            return responsePlanDTO;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PlanService.addPlan() : 에러 발생.");
        }

    }

    // 여행 일정 수정
    public PlanDTO updatePlan(int memberId, PlanDTO planDTO) {

        // 조건 체크
        if(planDTO.getState() > 1) {
            log.warn("PlanService.updatePlan() : 여행이 시작된 후엔 수정할 수 없습니다.");
            throw new RuntimeException("PlanService.updatePlan() : 여행이 시작된 후엔 수정할 수 없습니다.");
        }
//        checkCondition(memberId, planDTO.getStartDate(), planDTO.getEndDate());

        try {
            System.out.println(planDTO);
            // 엔티티에 저장
            Plan plan = planRepository.findById(planDTO.getPlanId());
            plan.setMemberId(memberId);
            plan.setTitle(planDTO.getTitle());
            plan.setPersonnel(planDTO.getPersonnel());
            plan.setPostDate(planDTO.getPostDate());
            plan.setDeadline(planDTO.getDeadline());
            plan.setStartDate(planDTO.getStartDate());
            plan.setEndDate(planDTO.getEndDate());
            plan.setNotice(planDTO.getNotice());
            plan.setTheme(planDTO.getTheme());
            plan.setPlace(planDTO.getPlace());
            planRepository.save(plan);

            // 저장한 엔티티 DTO에 담아서 리턴
            PlanDTO responsePlanDTO = PlanDTO.builder()
                    .planId(planDTO.getPlanId())
                    .title(plan.getTitle())
                    .personnel(plan.getPersonnel())
                    .postDate(plan.getPostDate())
                    .deadline(plan.getDeadline())
                    .startDate(plan.getStartDate())
                    .endDate(plan.getEndDate())
                    .notice(plan.getNotice())
                    .theme(plan.getTheme())
                    .place(plan.getPlace())
                    .build();
            // 기존 디테일 삭제
            planDetailService.deleteDetail(planDTO.getPlanDetailDTOList().get(0).getPlanId());

            // 새로 추가
            List<PlanDetailDTO> planDetailDTOList = new ArrayList<>();
            for (PlanDetailDTO planDetailDTO : planDTO.getPlanDetailDTOList()) {

                PlanDetail planDetail = new PlanDetail();
                planDetail.setPlanId(planDetailDTO.getPlanId());
                planDetail.setDetailDate(planDetailDTO.getDetailDate());
                planDetail.setContent(planDetailDTO.getContent());

                planDetailRepository.save(planDetail);

                // 이미지가 있는 경우
                if (planDetailDTO.checkFileNull()) {

                    MultipartFile multipartFile = planDetailDTO.getFile().get(0);
                    String current_date = null;

                    if (!multipartFile.isEmpty()) {
                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                        current_date = now.format(dateTimeFormatter);

                        String absolutePath = "C:" + File.separator + "withMeImgs" + File.separator + "planDetail";

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

                            planDetail.setDetailImg(new_file_name + originalFileExtension);

                            planDetailRepository.save(planDetail);

                            file = new File(absolutePath + File.separator + new_file_name + originalFileExtension);
                            multipartFile.transferTo(file);

                            file.setWritable(true);
                            file.setReadable(true);
                            break;
                        }
                    }
                } // 이미지처리 끝

                PlanDetailDTO tempDTO = new PlanDetailDTO(planDetail);
                planDetailDTOList.add(tempDTO);
            } // for문 끝

            // plandto에 담아서 보내기
            responsePlanDTO.setPlanDetailDTOList(planDetailDTOList);

            return responsePlanDTO;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PlanService.updatePlan() : 에러 발생.");
        }

    }

    // 여행 일정 삭제
    public void deletePlan(int memberId, PlanDTO planDTO) {

        if(1 != planRepository.countByPlanIdAndMemberId(memberId, planDTO.getPlanId())) {
            log.warn("PlanService.deletePlan() : 해당 Plan의 작성자가 아닙니다.");
            throw new RuntimeException("PlanService.deletePlan() : 해당 Plan의 작성자가 아닙니다.");
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
            // 조회수 증가
            plan.setHit(plan.getHit() + 1);
            planRepository.save(plan);
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

            // 총 참가 회원 수 가져오기
            long cnt = planMembersRepository.countByPlanId(planDTO.getPlanId());
            responsePlanDTO.setParticipant(cnt);

            // 작성회원 정보 가져오기
            Member member = memberRepository.findByMemberId(plan.getMemberId());
            responsePlanDTO.setMemberId(member.getMemberId());
            responsePlanDTO.setNickname(member.getNickname());

            return responsePlanDTO;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PlanService.viewPlan() : 에러 발생.");
        }

    }

    // Plan 지역으로 검색하기
    public List<PlanDTO> planSearch(final String keyword, final Pageable pageable) throws Exception{
        try{
            Page<Plan> pPlan = planRepository.findAllByPlaceLikeOrderByPostDateDesc(keyword, pageable);
            List<Plan> lPlan = pPlan.getContent();
            List<PlanDTO> resultList = new ArrayList<>();
            for(Plan plan : lPlan){
                Member member = memberRepository.findByMemberId(plan.getMemberId());
                long cnt = planMembersRepository.countByPlanId(plan.getPlanId());
                PlanDTO planDTO = PlanDTO.builder().deadline(plan.getDeadline()).planId(plan.getPlanId())
                        .startDate(plan.getStartDate()).endDate(plan.getEndDate()).participant(cnt)
                        .personnel(plan.getPersonnel()).state(plan.getState()).postDate(plan.getPostDate())
                        .hit(plan.getHit()).title(plan.getTitle()).nickname(member.getNickname()).build();
                resultList.add(planDTO);
            }
            return resultList;
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }



    // 나의 여행 일정 리스트
    public List<PlanDTO> listPlan(int memberId, Pageable pageable) {

        try {

            Page<Plan> planPage = planRepository.findByMemberIdOrderByPostDateDesc(memberId, pageable);
            List<Plan> planList = planPage.getContent();
            List<PlanDTO> planDTOList = new ArrayList<>();

            for (Plan plan : planList) {
                PlanDTO planDTO = PlanDTO.builder()
                        .planId(plan.getPlanId())
                        .title(plan.getTitle())
                        .postDate(plan.getPostDate())
                        .deadline(plan.getDeadline())
                        .startDate(plan.getStartDate())
                        .endDate(plan.getEndDate())
                        .state(plan.getState())
                        .build();
                // 구성원 평가 작성 상태
                planDTO.setReviewMemberState(planMembersRepository.selectCheckByPlanIdAndMemberId(plan.getPlanId(), memberId));
                // 일정 리뷰 작성 상태
                switch (plan.getState()) {
                    case 0 :
                    case 1 :
                    case 2 : // 여행 끝나지 않음
                        planDTO.setReviewPlanState(0);
                        break;
                    case 3 : // 여행 끝난 후 작성하지 않았다면 1 , 했다면 2 할당
                        if(0 == reviewPlanRepository.countByPlanIdAndReviewer(plan.getPlanId(), memberId)) planDTO.setReviewPlanState(1);
                        else planDTO.setReviewPlanState(2);
                        break;
                }
                // dto 리스트에 dto 추가
                planDTOList.add(planDTO);
            }
            return planDTOList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PlanService.listPlan() : 에러 발생.");
        }
    }
}
