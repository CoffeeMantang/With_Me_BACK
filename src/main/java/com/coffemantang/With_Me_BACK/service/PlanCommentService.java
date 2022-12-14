package com.coffemantang.With_Me_BACK.service;

import com.coffemantang.With_Me_BACK.dto.PlanCommentDTO;
import com.coffemantang.With_Me_BACK.model.PlanComment;
import com.coffemantang.With_Me_BACK.persistence.MemberRepository;
import com.coffemantang.With_Me_BACK.persistence.PlanCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanCommentService {

    private final PlanCommentRepository planCommentRepository;

    private final MemberRepository memberRepository;

    // 댓글 작성
    public void addComment(int memberId, PlanCommentDTO planCommentDTO, int planId) {

        try {

            PlanComment planComment = new PlanComment();
            planComment.setPlanId(planId);
            planComment.setMemberId(memberId);
            planComment.setContent(planCommentDTO.getContent());
            planComment.setStage(0);
            planComment.setTurn(0);
            planComment.setCommentDate(LocalDateTime.now());
            planComment.setGroupNum(planCommentRepository.save(planComment).getPlanCommentId());
            planCommentRepository.save(planComment);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PlanCommentService.addComment() : 에러 발생.");
        }
    }

    // 대댓글 작성
    public void addRecomment(int memberId, PlanCommentDTO planCommentDTO, String content) {

        try {
            // 대댓글 갯수 가져오기
            long count = planCommentRepository.countByGroupNum(planCommentDTO.getGroupNum());
            Integer maxTurn = 0;
            if(count == 0){
                maxTurn = 0;
            }else{
                maxTurn = planCommentRepository.selectMaxTurnByPlanIdAndGroupNum(planCommentDTO.getPlanId(), planCommentDTO.getGroupNum());
            }



            PlanComment planComment = new PlanComment();
            planComment.setMemberId(memberId);
            planComment.setPlanId(planCommentDTO.getPlanId());
            planComment.setContent(content);
            planComment.setStage(1);
            planComment.setTurn(maxTurn + 1);
            planComment.setGroupNum(planCommentDTO.getGroupNum());
            planComment.setCommentDate(LocalDateTime.now());
            planCommentRepository.save(planComment);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PlanCommentService.addRecomment() : 에러 발생.");
        }

    }

    // 댓글 리스트
    public List<PlanCommentDTO> listComment(PlanCommentDTO planCommentDTO, Pageable pageable) {

        try {

            Page<PlanComment> planCommentPage = planCommentRepository.findByPlanIdAndStageOrderByCommentDate(
                    planCommentDTO.getPlanId(), 0, pageable);
            List<PlanComment> planCommentList = planCommentPage.getContent();
            List<PlanCommentDTO> planCommentDTOList = new ArrayList<>();
            for (PlanComment planComment : planCommentList) {
                PlanCommentDTO newPlanCommentDto = PlanCommentDTO.builder()
                            .planId(planComment.getPlanId())
                            .content(planComment.getContent())
                            .stage(planComment.getStage())
                            .turn(planComment.getTurn())
                            .groupNum(planComment.getGroupNum())
                            .commentDate(planComment.getCommentDate())
                            .nickname(memberRepository.findNicknameByMemberId(planComment.getMemberId()))
                        .planCommentId(planComment.getPlanCommentId())
                            .build();
                planCommentDTOList.add(newPlanCommentDto);
            }

            return planCommentDTOList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PlanCommentService.listComment() : 에러 발생.");
        }
    }

    // 대댓글 리스트
    public List<PlanCommentDTO> listRecomment(PlanCommentDTO planCommentDTO, Pageable pageable) {

        try {

            Page<PlanComment> planCommentPage = planCommentRepository.findByPlanIdAndGroupNumAndStageOrderByTurn(
                    planCommentDTO.getPlanId(), planCommentDTO.getGroupNum(), 1, pageable);
            List<PlanComment> planCommentList = planCommentPage.getContent();
            List<PlanCommentDTO> planCommentDTOList = new ArrayList<>();
            for (PlanComment planComment : planCommentList) {
                PlanCommentDTO newPlanCommentDto = PlanCommentDTO.builder()
                        .planCommentId(planComment.getPlanCommentId())
                        .planId(planComment.getPlanId())
                        .content(planComment.getContent())
                        .stage(planComment.getStage())
                        .turn(planComment.getTurn())
                        .groupNum(planComment.getGroupNum())
                        .commentDate(planComment.getCommentDate())
                        .nickname(memberRepository.findNicknameByMemberId(planComment.getMemberId()))
                        .build();
                planCommentDTOList.add(newPlanCommentDto);
            }

            return planCommentDTOList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PlanCommentService.listRecomment() : 에러 발생.");
        }
    }

    public void deleteComment(int memberId, PlanCommentDTO planCommentDTO) {

        try {

            PlanComment planComment = planCommentRepository.findByPlanCommentIdAndMemberId(planCommentDTO.getPlanCommentId(), memberId);
            log.warn("planCommentID : " + planCommentDTO.getPlanCommentId());
            int stage = planComment.getStage();
            if (stage == 0){
                List<PlanComment> planCommentList = planCommentRepository.findByPlanIdAndGroupNum(planComment.getPlanId(), planComment.getGroupNum());
                planCommentRepository.deleteAll(planCommentList);
            } else if (stage == 1) {
                planCommentRepository.delete(planComment);
            }


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PlanCommentService.deleteComment() : 에러 발생.");
        }
    }
}
