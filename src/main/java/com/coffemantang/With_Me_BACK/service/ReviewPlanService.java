package com.coffemantang.With_Me_BACK.service;

import com.coffemantang.With_Me_BACK.dto.CheckDTO;
import com.coffemantang.With_Me_BACK.dto.ReviewPlanDTO;
import com.coffemantang.With_Me_BACK.dto.ReviewPlanImgDTO;
import com.coffemantang.With_Me_BACK.model.ReviewPlan;
import com.coffemantang.With_Me_BACK.model.ReviewPlanImg;
import com.coffemantang.With_Me_BACK.persistence.MemberRepository;
import com.coffemantang.With_Me_BACK.persistence.PlanRepository;
import com.coffemantang.With_Me_BACK.persistence.ReviewPlanImgRepository;
import com.coffemantang.With_Me_BACK.persistence.ReviewPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewPlanService {

    private final ReviewPlanRepository reviewPlanRepository;

    private final ReviewPlanImgRepository reviewPlanImgRepository;

    private final PlanRepository planRepository;

    private final PlanService planService;

    private final MemberRepository memberRepository;

    // 리뷰 작성
    public void addReviewPlan(int memberId, ReviewPlanDTO reviewPlanDTO) {

        planService.checkState(new CheckDTO(reviewPlanDTO.getPlanId()));
        if ( 0 < reviewPlanRepository.countByPlanIdAndReviewer(reviewPlanDTO.getPlanId(), memberId)) {
            log.warn("ReviewMemberService.addReviewPlan() : 이미 해당 여행에 대한 리뷰를 작성했습니다.");
            throw new RuntimeException("ReviewMemberService.addReviewPlan() : 이미 해당 여행에 대한 리뷰를 작성했습니다.");
        } else if (0 == planRepository.countByPlanIdAndState(reviewPlanDTO.getPlanId(), 3)){
            log.warn("ReviewMemberService.addReviewPlan() : 해당 여행이 아직 종료되지 않았습니다.");
            throw new RuntimeException("ReviewMemberService.addReviewPlan() : 해당 여행이 아직 종료되지 않았습니다.");
        }

        try {

            // 엔티티 생성
            ReviewPlan reviewPlan = new ReviewPlan();
            reviewPlan.setReviewer(memberId);
            reviewPlan.setPlanId(reviewPlanDTO.getPlanId());
            reviewPlan.setRating(reviewPlanDTO.getRating());
            reviewPlan.setContent(reviewPlanDTO.getContent());

            // 저장한 아이디 가져오기
            int reviewPlanId = reviewPlanRepository.save(reviewPlan).getReviewPlanId();

            // 이미지가 있다면 true
            if (reviewPlanDTO.checkFileNull()) {

                // 반환할 파일 리스트
                List<ReviewPlanImg> fileList = new ArrayList<>();
                // 전달되어 온 파일이 존재할 경우
                String current_date = null;
                if (!CollectionUtils.isEmpty(reviewPlanDTO.getFiles())) {
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    current_date = now.format(dateTimeFormatter);

                    // 프로젝트 디렉터리 내의 저장을 위한 절대 경로 설정
                    // 경로 구분자 File.separator 사용
                    // String absolutePath = new File("").getAbsolutePath() + File.separator + File.separator;
                    String absolutePath = "C:" + File.separator + "withMeImgs" + File.separator + "review";

                    // 파일을 저장할 세부 경로 지정
                    String path = absolutePath;
                    File file = new File(path);

                    // 디렉토리가 존재하지 않을 경우
                    if (!file.exists()) {
                        // 디렉토리 생성
                        boolean wasSuccessful = file.mkdirs();

                        // 디렉토리 생성에 실패했을 경우
                        if (!wasSuccessful) {
                            log.warn("file: was not successful");
                        }
                    }

                    // 다중 파일 처리
                    int cnt = 1;
                    for (MultipartFile multipartFile : reviewPlanDTO.getFiles()) {

                        // 파일의 확작자 추출
                        String originalFileExtension;
                        String contentType = multipartFile.getContentType();

                        // 확장자명이 존재하지 않을 경우 처리하지 않음
                        if (ObjectUtils.isEmpty(contentType)) {
                            break;
                        } else {
                            // 확장자가 jpeg, png인 파일들만 받아서 처리
                            if (contentType.contains("image/jpeg")) {
                                originalFileExtension = ".jpg";
                            } else if (contentType.contains("images/png")) {
                                originalFileExtension = ".png";
                            } else { // 다른 확장자일 경우 처리하지 않음
                                break;
                            }
                        }

                        // 파일명 중복 피하기 위해 나노초까지 얻어와 지정
                        //String new_file_name = System.nanoTime() + originalFileExtension; // 나노초 + 확장자

                        // 파일명은 아이디 + _숫자로
                        String new_file_name = String.valueOf(reviewPlanId) + "_" + String.valueOf(cnt);

                        // 앤티티 생성
                        ReviewPlanImg reviewPlanImg = new ReviewPlanImg();
                        reviewPlanImg.setReviewPlanId(reviewPlanId);
                        reviewPlanImg.setPath(new_file_name + originalFileExtension);

                        // 생성 후 리스트에 추가
                        fileList.add(reviewPlanImg);

                        // 업로드한 파일 데이터를 지정한 파일에 저장
                        file = new File(path + File.separator + new_file_name + originalFileExtension);
                        multipartFile.transferTo(file);

                        // 파일 권한 설정
                        file.setWritable(true);
                        file.setReadable(true);

                        // 엔티티 저장
                        reviewPlanImgRepository.save(reviewPlanImg);
                        cnt++;

                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ReviewMemberService.addReviewPlan() : 에러발생");
        }
    }


    // 여행 리뷰 수정
    public ReviewPlanDTO updateReviewPlan(int memberId, ReviewPlanDTO reviewPlanDTO) {

        if(memberId != memberRepository.findIdByNickname(reviewPlanDTO.getReviewerNickname())) {
            log.warn("ReviewMemberService.updateReviewPlan() : 로그인된 유저와 리뷰 작성자가 다릅니다.");
            throw new RuntimeException("ReviewMemberService.updateReviewPlan() : 로그인된 유저와 리뷰 작성자가 다릅니다.");
        }

        try {

            ReviewPlan reviewPlan = reviewPlanRepository.findByReviewPlanId(reviewPlanDTO.getReviewPlanId());
            reviewPlan.setRating(reviewPlanDTO.getRating());
            reviewPlan.setContent(reviewPlanDTO.getContent());
            reviewPlanRepository.save(reviewPlan);
            int reviewPlanId = reviewPlanDTO.getReviewPlanId();

            // 이미지가 있다면 true
            if (reviewPlanDTO.checkFileNull()) {

                // 반환할 파일 리스트
                List<ReviewPlanImg> fileList = new ArrayList<>();
                // 전달되어 온 파일이 존재할 경우
                String current_date = null;
                if (!CollectionUtils.isEmpty(reviewPlanDTO.getFiles())) {
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    current_date = now.format(dateTimeFormatter);

                    // 프로젝트 디렉터리 내의 저장을 위한 절대 경로 설정
                    // 경로 구분자 File.separator 사용
                    // String absolutePath = new File("").getAbsolutePath() + File.separator + File.separator;
                    String absolutePath = "C:" + File.separator + "withMeImgs" + File.separator + "review";

                    // 파일을 저장할 세부 경로 지정
                    String path = absolutePath;
                    File file = new File(path);

                    // 디렉토리가 존재하지 않을 경우
                    if (!file.exists()) {
                        // 디렉토리 생성
                        boolean wasSuccessful = file.mkdirs();

                        // 디렉토리 생성에 실패했을 경우
                        if (!wasSuccessful) {
                            log.warn("file: was not successful");
                        }
                    }

                    // 다중 파일 처리
                    int cnt = 1;
                    for (MultipartFile multipartFile : reviewPlanDTO.getFiles()) {

                        // 파일의 확작자 추출
                        String originalFileExtension;
                        String contentType = multipartFile.getContentType();

                        // 확장자명이 존재하지 않을 경우 처리하지 않음
                        if (ObjectUtils.isEmpty(contentType)) {
                            break;
                        } else {
                            // 확장자가 jpeg, png인 파일들만 받아서 처리
                            if (contentType.contains("image/jpeg")) {
                                originalFileExtension = ".jpg";
                            } else if (contentType.contains("images/png")) {
                                originalFileExtension = ".png";
                            } else { // 다른 확장자일 경우 처리하지 않음
                                break;
                            }
                        }

                        // 파일명 중복 피하기 위해 나노초까지 얻어와 지정
                        //String new_file_name = System.nanoTime() + originalFileExtension; // 나노초 + 확장자

                        // 파일명은 아이디 + _숫자로
                        String new_file_name = String.valueOf(reviewPlanId) + "_" + String.valueOf(cnt);

                        // 앤티티 생성
                        ReviewPlanImg reviewPlanImg = reviewPlanImgRepository.findTop1ByReviewPlanId(reviewPlanId);
                        // 기존에 파일이 있는 경우 기존 파일을 제거하고 진행
                        if(reviewPlanImg.getPath() != null && reviewPlanImg.getPath() != ""){
                            String tempPath = absolutePath + File.separator + reviewPlanImg.getPath();
                            File delFile = new File(tempPath);
                            // 해당 파일이 존재하는지 한번 더 체크 후 삭제
                            if(delFile.isFile()){
                                delFile.delete();
                            }
                        }

                        reviewPlanImg.setReviewPlanId(reviewPlanId);
                        reviewPlanImg.setPath(new_file_name + originalFileExtension);

                        // 생성 후 리스트에 추가
                        fileList.add(reviewPlanImg);

                        // 업로드한 파일 데이터를 지정한 파일에 저장
                        file = new File(path + File.separator + new_file_name + originalFileExtension);
                        multipartFile.transferTo(file);

                        // 파일 권한 설정
                        file.setWritable(true);
                        file.setReadable(true);

                        // 엔티티 저장
                        reviewPlanImgRepository.save(reviewPlanImg);
                        cnt++;

                    }

                }

            }
            return new ReviewPlanDTO(reviewPlan);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ReviewMemberService.updateReviewPlan() : 에러 발생.");
        }

    }

    // 여행 리뷰 삭제
    public void deleteReviewPlan(int memberId, ReviewPlanDTO reviewPlanDTO) {

        if(memberId != memberRepository.findIdByNickname(reviewPlanDTO.getReviewerNickname())) {
            log.warn("ReviewMemberService.deleteReviewPlan() : 로그인된 유저와 리뷰 작성자가 다릅니다.");
            throw new RuntimeException("ReviewMemberService.deleteReviewPlan() : 로그인된 유저와 리뷰 작성자가 다릅니다.");
        }

        try {

            ReviewPlan reviewPlan = reviewPlanRepository.findByReviewPlanId(reviewPlanDTO.getReviewPlanId());
            List<ReviewPlanImg> reviewPlanImgList = reviewPlanImgRepository.findByReviewPlanId(reviewPlanDTO.getReviewPlanId());
            // 이미지가 있는 경우
            if (reviewPlanImgList != null) {
                for (ReviewPlanImg reviewPlanImg : reviewPlanImgList) {
                    String tempPath = "C:" + File.separator + "withMeImgs" + File.separator + "review" + File.separator + reviewPlanImg.getPath();
                    File delFile = new File(tempPath);
                    // 해당 파일이 존재하는지 한번 더 체크 후 삭제
                    if(delFile.isFile()){
                        delFile.delete();
                    }
                    reviewPlanImgRepository.delete(reviewPlanImg);
                }
            }
            reviewPlanRepository.delete(reviewPlan);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ReviewMemberService.deleteReviewPlan() : 에러 발생.");
        }

    }

    // 여행 리뷰 보기
    public ReviewPlanDTO viewReviewPlan(ReviewPlanDTO reviewPlanDTO) {

        try {

            // reviewPlan 엔티티 가져오기
            ReviewPlan reviewPlan = reviewPlanRepository.findByReviewPlanId(reviewPlanDTO.getReviewPlanId());
            // 이미지 리스트 가져와서 이미지 설정
            List<ReviewPlanImg> reviewPlanImgList = reviewPlanImgRepository.findByReviewPlanId(reviewPlan.getReviewPlanId());
            List<ReviewPlanImgDTO> reviewPlanImgDTOList = new ArrayList<>();

            for (ReviewPlanImg reviewPlanImg : reviewPlanImgList) {
                reviewPlanImg.setPath("http://localhost:8080/withMeImgs/review/" + reviewPlanImg.getPath());
                ReviewPlanImgDTO reviewPlanImgDTO = new ReviewPlanImgDTO(reviewPlanImg);
                reviewPlanImgDTOList.add(reviewPlanImgDTO);
            }

            // reviewPlanDTO 생성 후 할당
            ReviewPlanDTO responseDTO = new ReviewPlanDTO(reviewPlan);
            responseDTO.setReviewPlanImgDTOList(reviewPlanImgDTOList);

            return responseDTO;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ReviewMemberService.viewReviewPlan() : 에러 발생.");
        }

    }

    // 프로필 대상이 쓴 여행 리뷰 리스트
    public List<ReviewPlanDTO> listReviewPlan(int memberId, Pageable pageable) {

        try {

            Page<ReviewPlan> reviewPlanPage = reviewPlanRepository.findByReviewer(memberId, pageable);
            List<ReviewPlan> reviewPlanList = reviewPlanPage.getContent();
            List<ReviewPlanDTO> reviewPlanDTOList = new ArrayList<>();

            for (ReviewPlan reviewPlan : reviewPlanList) {
                ReviewPlanDTO reviewPlanDTO = new ReviewPlanDTO(reviewPlan);
                reviewPlanDTOList.add(reviewPlanDTO);
            }
            return reviewPlanDTOList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ReviewMemberService.listReviewPlan() : 에러 발생.");
        }

    }

}
