package com.coffemantang.With_Me_BACK.service;

import com.coffemantang.With_Me_BACK.dto.PlanDTO;
import com.coffemantang.With_Me_BACK.dto.PlanDetailDTO;
import com.coffemantang.With_Me_BACK.model.PlanDetail;
import com.coffemantang.With_Me_BACK.persistence.PlanDetailRepository;
import com.coffemantang.With_Me_BACK.persistence.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanDetailService {

    private final PlanDetailRepository planDetailRepository;

    private final PlanRepository planRepository;

    // 여행 일정 디테일 추가
    public void addDetail(int memberId, PlanDTO planDetailDTOList) {

        PlanDetailDTO tempPlanDetailDTO = planDetailDTOList.getPlanDetailDTOList().get(0);
        int tempMemberId = planRepository.selectMemberIdById(tempPlanDetailDTO.getPlanId());

        if(memberId != tempMemberId) {
            log.warn("PlanDetailService.addDetail() : 로그인된 유저와 디테일 작성자가 다릅니다.");
            throw new RuntimeException("PlanDetailService.addDetail() : 로그인된 유저와 디테일 작성자 다릅니다.");
        }

        try {

            for (PlanDetailDTO planDetailDTO : planDetailDTOList.getPlanDetailDTOList()) {

                // 엔티티 빌더
                PlanDetail planDetail = PlanDetail.builder()
                        .planId(planDetailDTO.getPlanId())
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

            } // for문 끝

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PlanDetailService.addDetail() : 에러 발생.");
        }
    }
}
