package com.coffemantang.With_Me_BACK.service;

import com.coffemantang.With_Me_BACK.dto.ReviewMemberDTO;
import com.coffemantang.With_Me_BACK.model.ReviewMember;
import com.coffemantang.With_Me_BACK.persistence.MemberRepository;
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

    private final MemberRepository memberRepository;

    // 평가 작성
    public void addReview(int memberId, ReviewMemberDTO reviewMemberDTO) {

        // 여행은 종료되었는데 check가 0이면 1로 변경
        planMembersRepository.updateCheck1(memberId);

        int reviewed = memberRepository.findIdByNickname(reviewMemberDTO.getReviewedNickname());

        if (1 != planMembersRepository.selectCheckByPlanIdAndMemberId(reviewMemberDTO.getPlanId(), memberId)) {
            log.warn("ReviewMemberService.addReview() : 해당 여행이 끝나지 않았거나 평가 작성을 완료했습니다.");
            throw new RuntimeException("ReviewMemberService.addReview() : 해당 여행이 끝나지 않았거나 평가 작성을 완료했습니다.");
        } else if (0 < reviewMemberRepository.countByPlanIdAndReviewerAndReviewed(reviewMemberDTO.getPlanId(), memberId, reviewed)) {
            log.warn("ReviewMemberService.addReview() : 해당 유저에 대한 평가를 이미 작성했습니다.");
            throw new RuntimeException("ReviewMemberService.addReview() : 해당 유저에 대한 평가를 이미 작성했습니다.");
        }

        try {

            ReviewMember reviewMember = new ReviewMember();
            reviewMember.setReviewer(memberId);
            reviewMember.setReviewed(reviewed);
            reviewMember.setPlanId(reviewMemberDTO.getPlanId());
            reviewMember.setRating(reviewMemberDTO.getRating());
            reviewMember.setContent(reviewMemberDTO.getContent());
            reviewMemberRepository.save(reviewMember);

            // 해당 여행의 모든 구성원에 대한 평가를 완료했다면 이 유저의 planMembers.check를 2로 변경
            int planMemberCount = planMembersRepository.countByPlanIdAndNotMemberId(reviewMemberDTO.getPlanId(), memberId);
            int reviewerCount = reviewMemberRepository.countByPlanIdAndReviewer(reviewMemberDTO.getPlanId(), memberId);
            if (planMemberCount == reviewerCount) planMembersRepository.updateCheck2(reviewMemberDTO.getPlanId(), memberId, 2);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ReviewMemberService.addReview() : 에러 발생");
        }

    }

    // 평가 수정
    public ReviewMemberDTO updateReview(int memberId, ReviewMemberDTO reviewMemberDTO) {

        int reviewer = memberRepository.findIdByNickname(reviewMemberDTO.getReviewerNickname());
        if (memberId != reviewer) {
            log.warn("ReviewMemberService.updateReview() : 평가를 수정할 권한이 없습니다.(MemberId 틀림)");
            throw new RuntimeException("ReviewMemberService.updateReview() : 평가를 수정할 권한이 없습니다.(MemberId 틀림)");
        }

        try {

            ReviewMember reviewMember = reviewMemberRepository.findByReviewMemberId(reviewMemberDTO.getReviewMemberId());
            reviewMember.setRating(reviewMemberDTO.getRating());
            reviewMember.setContent(reviewMemberDTO.getContent());
            reviewMemberRepository.save(reviewMember);

            return ReviewMemberDTO.builder()
                    .reviewerNickname(reviewMemberDTO.getReviewerNickname())
                    .reviewedNickname(reviewMemberDTO.getReviewedNickname())
                    .rating(reviewMember.getRating())
                    .content(reviewMember.getContent())
                    .planId(reviewMemberDTO.getPlanId())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ReviewMemberService.updateReview() : 에러 발생");
        }

    }

    // 프로필 대상이 받은 리뷰 리스트
    public List<ReviewMemberDTO> listReview(String targetNickname, Pageable pageable) {

        try {

            Page<ReviewMember> reviewMemberPage = reviewMemberRepository.findByReviewed(memberRepository.findIdByNickname(targetNickname), pageable);
            List<ReviewMember> reviewMemberList =reviewMemberPage.getContent();
            List<ReviewMemberDTO> reviewMemberDTOList =new ArrayList<>();
            for (ReviewMember reviewMember : reviewMemberList) {
                ReviewMemberDTO reviewMemberDTO = ReviewMemberDTO.builder()
                                .rating(reviewMember.getRating())
                                .content(reviewMember.getContent())
                                .build();
                reviewMemberDTOList.add(reviewMemberDTO);
            }

            return reviewMemberDTOList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ReviewMemberService.listReview() : 에러 발생");
        }
    }
}
