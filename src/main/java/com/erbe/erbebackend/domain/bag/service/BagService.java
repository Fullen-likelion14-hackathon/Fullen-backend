package com.erbe.erbebackend.domain.bag.service;

import com.erbe.erbebackend.domain.bag.dto.response.UserBagDetailResponse;
import com.erbe.erbebackend.domain.bag.dto.response.UserBagListResponse;
import com.erbe.erbebackend.domain.bag.entity.UserBag;
import com.erbe.erbebackend.domain.bag.exception.UserBagErrorCode;
import com.erbe.erbebackend.domain.bag.repository.UserBagRepository;
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
@Transactional(readOnly = true)
@Slf4j
public class BagService {

    private final UserBagRepository userBagRepository;
    private final UserRepository userRepository;

    // 소유한 가방 리스트 조회
    public List<UserBagListResponse> findAllUserBags(Long userId) {

        // 사용자가 존재하는지 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        // 응답 세팅
        List<UserBagListResponse> list = new ArrayList<>();
        for (UserBag userBag : userBagRepository.findAllByUser(user)) {
            list.add(UserBagListResponse.builder()
                    .userBagId(userBag.getId())
                    .bagName(userBag.getBagProduct().getBag().getName())
                    .bagFrontImgUrl(userBag.getBagProduct().getBag().getFrontImgUrl())
                    .build());
        }

        // 로그 출력
        log.info("[BagService] 사용자 소유 가방 리스트 조회 성공");

        return list;
    }

    // 사용자 소유 특정 가방 조회
    public UserBagDetailResponse findUserBagDetail(Long userBagId, Long userId) {

        // 가방이 존재하는지 조회
        UserBag userBag = userBagRepository.findById(userBagId)
                .orElseThrow(() -> new CustomException(UserBagErrorCode.USER_BAG_NOT_FOUND));

        // 사용자 본인 소유의 가방인지 확인
        if (!userBag.getUser().getId().equals(userId)) {
            log.warn("[BagService] 본인 소유의 가방이 아닙니다.");
            throw new CustomException(UserBagErrorCode.USER_BAG_ACCESS_DENIED);
        }

        // 로그 출력
        log.info("[BagService] 사용자 소유 가방 상세조회 성공: userBagId={}", userBagId);

        // 응답 세팅
        return UserBagDetailResponse.builder()
                .userBagId(userBag.getId())
                .bagName(userBag.getBagProduct().getBag().getName())
                .bagSize(userBag.getBagProduct().getBag().getSize())
                .bagFrontImgUrl(userBag.getBagProduct().getBag().getFrontImgUrl())
                .bagBackImgUrl(userBag.getBagProduct().getBag().getBackImgUrl())
                .build();
    }
}
