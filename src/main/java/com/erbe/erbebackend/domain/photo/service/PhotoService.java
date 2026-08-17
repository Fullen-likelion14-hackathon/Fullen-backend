package com.erbe.erbebackend.domain.photo.service;

import com.erbe.erbebackend.domain.nation.enums.Continent;
import com.erbe.erbebackend.domain.photo.dto.response.PhotoDetailResponse;
import com.erbe.erbebackend.domain.photo.dto.response.PhotoResponse;
import com.erbe.erbebackend.domain.photo.entity.Photo;
import com.erbe.erbebackend.domain.photo.exception.PhotoErrorCode;
import com.erbe.erbebackend.domain.photo.repository.PhotoRepository;
import com.erbe.erbebackend.domain.user.entity.User;
import com.erbe.erbebackend.domain.user.exception.UserErrorCode;
import com.erbe.erbebackend.domain.user.repository.UserRepository;
import com.erbe.erbebackend.global.exception.CustomException;
import com.erbe.erbebackend.global.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    public List<PhotoResponse> getUserPhoto(Continent scope, Long userId){

        log.info("[PhotoService] 유저 기반 사진 전체 조회 시작 - userId ={}", userId);

        User user = userRepository.findById(userId).orElseThrow(() ->{
            log.warn("[PhotoService] 유저를 찾을 수 없습니다 - userId : {}", userId);
            return new CustomException(UserErrorCode.USER_NOT_FOUND);
        });

        List<Photo> photos = new ArrayList<>();

        if(scope != null){
            photos = photoRepository.findAllByPostNationContinentAndPostUserOrderByPostCreatedDate(scope, user);
        }  else {
            photos = photoRepository.findAllByPostUserOrderByPostCreatedDate(user);
        }

        List<PhotoResponse> responseList = new ArrayList<>();

        // DTO 변환
        for(Photo photo : photos){
            responseList.add(toPhotoResponse(photo));
        }

        log.info("[PhotoService] 유저 기반 사진 전체 조회 종료 - photo count = {}", responseList.size());

        return responseList;
    }

    public PhotoDetailResponse getPhotoDetail(Long photoId, Long userId){

        Photo photo = photoRepository.findById(photoId).orElseThrow(() -> {
            log.warn("[PhotoService] 사진이 존재하지 않습니다 - photoId : {}", photoId);
            return new CustomException(PhotoErrorCode.PHOTO_NOT_FOUND);
        });

        // 사진 무단 조회 시도시 차단
        if(!(photo.getPost().getUser().getId().equals(userId))){
            log.warn("[PhotoService] 사진 무단 조회 시도 - photoId : {}, 무단 시도 userId : {}", photoId, userId);
            throw new CustomException(PhotoErrorCode.PHOTO_ACCESS_DENIED);
        }

        return toPhotoDetailResponse(photo);
    }

    private PhotoDetailResponse toPhotoDetailResponse(Photo photo){
        return PhotoDetailResponse.builder()
                .photoId(photo.getId())
                .imgURL(photo.getImgUrl())
                .nationKRName(photo.getPost().getNation().getKrName())
                .journeyType(photo.getPost().getJourney().getType())
                .date(photo.getPost().getCreatedDate())
                .build();
    }

    private PhotoResponse toPhotoResponse(Photo photo){
        return PhotoResponse.builder()
                .photoId(photo.getId())
                .imgURL(photo.getImgUrl())
                .build();
    }

    public String deletePhoto(Long photoId, Long userId) {

        Photo photo = photoRepository.findById(photoId).orElseThrow(() -> {
            log.warn("[PhotoService] 사진을 찾을 수 없습니다 - photoId : {}", photoId);
            return new CustomException(PhotoErrorCode.PHOTO_NOT_FOUND);
        });

        // 본인 소유의 사진이 아니면 삭제 불가능
        if(!(photo.getPost().getUser().getId().equals(userId))){
            log.warn("[PhotoService] 사진 무단 삭제 시도 - photoId : {}, 무단 삭제 시도 userId : {}", photoId, userId);
            throw new CustomException(PhotoErrorCode.PHOTO_ACCESS_DENIED);
        }

        // s3에서 삭제하기 위해 사진 URL 가져오기
        String s3URL = photo.getImgUrl();

        // s3 버킷에서 사진 삭제
        s3Uploader.delete(s3URL);

        // DB에서 사진 레코드 삭제
        photoRepository.delete(photo);

        return "사진 삭제 성공 - photoId : " + photoId;
    }
}
