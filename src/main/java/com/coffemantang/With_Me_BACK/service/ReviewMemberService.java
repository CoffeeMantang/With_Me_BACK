package com.coffemantang.With_Me_BACK.service;

import com.coffemantang.With_Me_BACK.dto.ReviewMemberDTO;
import com.coffemantang.With_Me_BACK.model.ReviewMember;
import com.coffemantang.With_Me_BACK.persistence.PlanMembersRepository;
import com.coffemantang.With_Me_BACK.persistence.ReviewMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewMemberService {

    private final ReviewMemberRepository reviewMemberRepository;

    private final PlanMembersRepository planMembersRepository;

    // 평가 작성
    public void addReview(int memberId, ReviewMemberDTO reviewMemberDTO) {

        if(memberId != reviewMemberDTO.getReviewer()) {
            log.warn("ReviewMemberService.addReview() : 로그인된 유저와 평가 작성자가 다릅니다.");
            throw new RuntimeException("ReviewMemberService.addReview() : 로그인된 유저와 평가 작성자가 다릅니다.");
        } else if (1 != planMembersRepository.selectCheckByPlanIdAndMemberId(reviewMemberDTO.getPlanId(), reviewMemberDTO.getReviewer())) {
            log.warn("ReviewMemberService.addReview() : 해당 여행이 끝나지 않았거나 평가 작성을 완료했습니다.");
            throw new RuntimeException("ReviewMemberService.addReview() : 해당 여행이 끝나지 않았거나 평가 작성을 완료했습니다.");
        } else if (0 < reviewMemberRepository.countByPlanIdAndReviewerAndReviewed(reviewMemberDTO.getPlanId(), reviewMemberDTO.getReviewer(), reviewMemberDTO.getReviewed())) {
            log.warn("ReviewMemberService.addReview() : 해당 유저에 대한 평가를 이미 작성했습니다.");
            throw new RuntimeException("ReviewMemberService.addReview() : 해당 유저에 대한 평가를 이미 작성했습니다.");
        }

        try {

            ReviewMember reviewMember = new ReviewMember();
            reviewMember.setReviewer(reviewMemberDTO.getReviewer());
            reviewMember.setReviewed(reviewMemberDTO.getReviewed());
            reviewMember.setPlanId(reviewMemberDTO.getPlanId());
            reviewMember.setRating(reviewMemberDTO.getRating());
            reviewMember.setContent(reviewMemberDTO.getContent());
            reviewMemberRepository.save(reviewMember);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ReviewMemberService.addReview() : 에러 발생");
        }

    }

    // 평가 수정
    public void updateReview(int memberId, ReviewMemberDTO reviewMemberDTO) {

        if(memberId != reviewMemberDTO.getReviewer()) {
            log.warn("ReviewMemberService.updateReview() : 로그인된 유저와 평가 수정자가 다릅니다.");
            throw new RuntimeException("ReviewMemberService.updateReview() : 로그인된 유저와 평가 수정자가 다릅니다.");
        }

        try {

            ReviewMember reviewMember = reviewMemberRepository.findByReviewMemberId(reviewMemberDTO.getReviewMemberId());
            reviewMember.setRating(reviewMemberDTO.getRating());
            reviewMember.setContent(reviewMemberDTO.getContent());
            reviewMemberRepository.save(reviewMember);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ReviewMemberService.updateReview() : 에러 발생");
        }

    }

    // 내가 받은 리뷰 리스트
    public List<ReviewMemberDTO> listReview(int memberId, Pageable pageable) {

        try {

            Page<ReviewMember> reviewMemberPage = reviewMemberRepository.findByReviewed(memberId, pageable);
            List<ReviewMember> reviewMemberList =reviewMemberPage.getContent();
            List<ReviewMemberDTO> reviewMemberDTOList =new ArrayList<>();
            for (ReviewMember reviewMember : reviewMemberList) {
                ReviewMemberDTO reviewMemberDTO = new ReviewMemberDTO(reviewMember);
                reviewMemberDTOList.add(reviewMemberDTO);
            }

            return reviewMemberDTOList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ReviewMemberService.listReview() : 에러 발생");
        }
    }
}
