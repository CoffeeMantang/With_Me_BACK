package com.coffemantang.With_Me_BACK.service;

import com.coffemantang.With_Me_BACK.dto.PlanDTO;
import com.coffemantang.With_Me_BACK.dto.PlanDetailDTO;
import com.coffemantang.With_Me_BACK.model.PlanDetail;
import com.coffemantang.With_Me_BACK.persistence.PlanDetailRepository;
import com.coffemantang.With_Me_BACK.persistence.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    // 일정 디테일 수정
    public List<PlanDetailDTO> updateDetail(int memberId, PlanDTO planDetailDTOList) {

        PlanDetailDTO tempPlanDetailDTO = planDetailDTOList.getPlanDetailDTOList().get(0);
        int tempMemberId = planRepository.selectMemberIdById(tempPlanDetailDTO.getPlanId());

        if(memberId != tempMemberId) {
            log.warn("PlanDetailService.updateDetail() : 로그인된 유저와 디테일 작성자가 다릅니다.");
            throw new RuntimeException("PlanDetailService.updateDetail() : 로그인된 유저와 디테일 작성자 다릅니다.");
        }

        try {

            // 기존 디테일 삭제
            deleteDetail(planDetailDTOList.getPlanDetailDTOList().get(0).getPlanId());

            List<PlanDetailDTO> responsePlanDetailDTO = new ArrayList<>();
            for (PlanDetailDTO planDetailDTO : planDetailDTOList.getPlanDetailDTOList()) {

                PlanDetail planDetail = new PlanDetail();
                planDetail.setPlanId(planDetailDTO.getPlanId());
                planDetail.setDetailDate(planDetailDTO.getDetailDate());
                planDetail.setContent(planDetailDTO.getContent());

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

                            String new_file_name = String.valueOf(planDetailId);

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
                responsePlanDetailDTO.add(tempDTO);
            } // for문 끝

            return responsePlanDetailDTO;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PlanDetailService.addDetail() : 에러 발생.");
        }

    }

    // 일정 디테일 삭제
    public void deleteDetail(int planId) {

        try {

            List<PlanDetail> planDetailList = planDetailRepository.findByPlanIdOrderByDetailDate(planId);
            for (PlanDetail planDetail : planDetailList) {
                if (planDetail.getDetailImg() != null) {
                    String tempPath = "C:" + File.separator + "withMeImgs" + File.separator + "planDetail" + File.separator + planDetail.getDetailImg();
                    File delFile = new File(tempPath);
                    // 해당 파일이 존재하는지 한번 더 체크 후 삭제
                    if(delFile.isFile()){
                        delFile.delete();
                    }
                }
                planDetailRepository.delete(planDetail);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PlanDetailService.deleteDetail() : 에러 발생.");
        }
    }

    // 일정 디테일 가져오기
    public PlanDetailDTO getPlanDetail(final int planDetailId) throws Exception{
        try{
            PlanDetail planDetail = planDetailRepository.findById(planDetailId);
            PlanDetailDTO planDetailDTO = PlanDetailDTO.builder().planDetailId(planDetailId)
                    .detailDate(planDetail.getDetailDate())
                    .detailImg("http://localhost:8080/withMeImgs/planDetail/" + planDetail.getDetailImg())
                    .content(planDetail.getContent())
                    .build();
            return planDetailDTO;
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("PlanDetailService.deleteDetail() : 에러 발생.");
        }
    }

    // 일정 디테일 하나만 수정
    public void updateOneDetail(int memberId, PlanDetailDTO planDetailDTOList) {

        PlanDetailDTO tempPlanDetailDTO = planDetailDTOList;

        // 추후본인확인 추가

        try {
            // entity 가져오기
            PlanDetail planDetail = planDetailRepository.findByPlanDetailId(tempPlanDetailDTO.getPlanDetailId());

            // entity 내용 수정하기
            planDetail.setContent(tempPlanDetailDTO.getContent());
            // 이미지 수정되었는지 체크
            if(tempPlanDetailDTO.checkFileNull()){ // 들어온 이미지가 있으면
                int planDetailId = tempPlanDetailDTO.getPlanDetailId();

                List<MultipartFile> multipartFiles = tempPlanDetailDTO.getFile();
                // 전달되어 온 파일이 존재할 경우
                String current_date = null;
                if (!CollectionUtils.isEmpty(multipartFiles)) {
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    current_date = now.format(dateTimeFormatter);

                    // 프로젝트 디렉터리 내의 저장을 위한 절대 경로 설정
                    // 경로 구분자 File.separator 사용
                    // String absolutePath = new File("").getAbsolutePath() + File.separator + File.separator;

                    String absolutePath = "C:" + File.separator + "withMeImgs" + File.separator + "planDetail";

                    // 파일을 저장할 세부 경로 지정
                    String path = absolutePath;
                    File file = new File(path);

                    // 디렉터리가 존재하지 않을 경우
                    if (!file.exists()) {
                        boolean wasSuccessful = file.mkdirs(); // 디렉터리 생성

                        // 디렉터리 생성에 실패했을 경우
                        if (!wasSuccessful) {
                            log.warn("file: was not successful");
                        }
                    }

                    // 다중 파일 처리
                    int cnt = 1;
                    for (MultipartFile multipartFile : multipartFiles) {
                        // 파일의 확장자 추출
                        String originalFileExtension;
                        String contentType = multipartFile.getContentType();

                        // 확장자명이 존재하지 않을 경우 처리하지 않음
                        if (ObjectUtils.isEmpty(contentType)) {
                            break;
                        } else { // 확장자가 jpeg, png인 파일들만 받아서 처리
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
                        String new_file_name = String.valueOf(planDetailId);

                        // 기존에 파일이 있는 경우 기존 파일을 제거하고 진행
                        if(planDetail.getDetailImg() != null && planDetail.getDetailImg() != ""){
                            String tempPath = absolutePath + File.separator + planDetail.getDetailImg();
                            File delFile = new File(tempPath);
                            // 해당 파일이 존재하는지 한번 더 체크 후 삭제
                            if(delFile.isFile()){
                                delFile.delete();
                            }
                        }

                        // 추후 다중파일업로드를 위해 아래 코드 수정예정

                        // 메뉴 엔티티 수정
                        planDetail.setDetailImg(new_file_name + originalFileExtension);

                        // 업로드 한 파일 데이터를 지정한 파일에 저장
                        file = new File(path + File.separator + new_file_name + originalFileExtension);
                        multipartFile.transferTo(file);

                        // 파일 권한 설정
                        file.setWritable(true);
                        file.setReadable(true);

                    }
                }
            }
            // 엔티티 저장
            planDetailRepository.save(planDetail);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PlanDetailService.addDetail() : 에러 발생.");
        }

    }
}
