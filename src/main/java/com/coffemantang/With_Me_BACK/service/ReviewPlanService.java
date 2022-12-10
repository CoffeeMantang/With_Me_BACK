package com.coffemantang.With_Me_BACK.service;

import com.coffemantang.With_Me_BACK.dto.ReviewPlanDTO;
import com.coffemantang.With_Me_BACK.model.ReviewPlan;
import com.coffemantang.With_Me_BACK.model.ReviewPlanImg;
import com.coffemantang.With_Me_BACK.persistence.ReviewPlanImgRepository;
import com.coffemantang.With_Me_BACK.persistence.ReviewPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
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

    // 리뷰 작성
    public void addReview(int memberId, List<MultipartFile> multipartFileList, @RequestBody ReviewPlanDTO reviewPlanDTO) {

        if(memberId != reviewPlanDTO.getReviewer()) {
            log.warn("ReviewMemberService.addReview() : 로그인된 유저와 리뷰 작성자가 다릅니다.");
            throw new RuntimeException("ReviewMemberService.addReview() : 로그인된 유저와 리뷰 작성자가 다릅니다.");
        }

        try {

            // 엔티티 생성
            ReviewPlan reviewPlan = new ReviewPlan();
            reviewPlan.setReviewer(reviewPlanDTO.getReviewer());
            reviewPlan.setPlanId(reviewPlanDTO.getPlanId());
            reviewPlan.setRating(reviewPlanDTO.getRating());
            reviewPlan.setContent(reviewPlanDTO.getContent());

            // 저장한 아이디 가져오기
            int reviewPlanId = reviewPlanRepository.save(reviewPlan).getReviewPlanId();

            // 반환할 파일 리스트
            List<ReviewPlanImg> fileList = new ArrayList<>();
            // 전달되어 온 파일이 존재할 경우
            String current_date = null;
            if(!CollectionUtils.isEmpty(multipartFileList)) {
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
                for (MultipartFile multipartFile : multipartFileList) {

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
                    reviewPlanImg.setPath(new_file_name);

                    // 생성 후 리스트에 추가
                    fileList.add(reviewPlanImg);

                    // 업로드한 파일 데이터를 지정한 파일에 저장
                    file = new File(path + File.separator + new_file_name);
                    multipartFile.transferTo(file);

                    // 파일 권한 설정
                    file.setWritable(true);
                    file.setReadable(true);

                    // 엔티티 저장
                    reviewPlanImgRepository.save(reviewPlanImg);
                    cnt ++;

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
