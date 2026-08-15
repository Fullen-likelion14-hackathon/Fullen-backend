package com.erbe.erbebackend.domain.photo.service;

import com.erbe.erbebackend.domain.photo.dto.response.PhotoResponse;
import com.erbe.erbebackend.domain.photo.entity.Photo;
import com.erbe.erbebackend.domain.photo.repository.PhotoRepository;
import com.erbe.erbebackend.domain.user.entity.User;
import com.erbe.erbebackend.domain.user.exception.UserErrorCode;
import com.erbe.erbebackend.domain.user.repository.UserRepository;
import com.erbe.erbebackend.global.exception.CustomException;
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

    public List<PhotoResponse> getUserPhoto(Long userId){

        log.info("[PhotoService] 유저 기반 사진 전체 조회 시작 - userId ={}", userId);

        User user = userRepository.findById(userId).orElseThrow(() ->{
            log.warn("[PhotoService] 유저를 찾을 수 없습니다 - userId : {}", userId);
            return new CustomException(UserErrorCode.USER_NOT_FOUND);
        });

        List<Photo> photos = photoRepository.findAllByPostUser(user);

        List<PhotoResponse> responseList = new ArrayList<>();

        // DTO 변환
        for(Photo photo : photos){
            responseList.add(toPhotoResponse(photo));
        }

        log.info("[PhotoService] 유저 기반 사진 전체 조회 종료 - photo count = {}", responseList.size());

        return responseList;
    }

    private PhotoResponse toPhotoResponse(Photo photo){
        return PhotoResponse.builder()
                .photoId(photo.getId())
                .imgURL(photo.getImgUrl())
                .build();
    }
}
