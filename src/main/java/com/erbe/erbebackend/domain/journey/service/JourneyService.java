package com.erbe.erbebackend.domain.journey.service;

import com.erbe.erbebackend.domain.journey.dto.response.JourneyMapPinResponse;
import com.erbe.erbebackend.domain.journey.dto.response.JourneyResponse;
import com.erbe.erbebackend.domain.journey.entity.Journey;
import com.erbe.erbebackend.domain.journey.exception.JourneyErrorCode;
import com.erbe.erbebackend.domain.journey.repository.JourneyRepository;
import com.erbe.erbebackend.domain.user.entity.User;
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
public class JourneyService {

    private final JourneyRepository journeyRepository;
    private final UserRepository userRepository;

    public JourneyResponse findJourneyById(Long id, Long userId) {

        log.info("[JourneyService] 여행 단일 조회 - 시작");

        // DB에서 여행 조회
        Journey journey = journeyRepository.findById(id).orElseThrow(() -> {
            log.warn("[JourneyService] 여행 조회 실패 - 존재하지 않는 여행 journeyId: {}", id);
            return new CustomException(JourneyErrorCode.JOURNEY_NOT_FOUND);
        });

        // 본인의 여행이 아닌 다른 사람의 여행을 조회하려하면 X
        if(journey.getUser().getId() != userId) {
            log.warn("[JourneyService] 여행 조회 실패 - 타인 여행 조회 시도 journeyId : {}, userId: {}", id, userId);
            throw new CustomException(JourneyErrorCode.NOT_JOURNEY_OWNER);
        }

        // DTO 변환
        JourneyResponse response = toJourneyResponse(journey);

        // DTO 반환
        return response;
    }

    public List<JourneyResponse> findAllJourneys(Long userId) {

        log.info("[JourneyService] 모든 여행 조회 - 시작");

        // 유저 조회
        User user = userRepository.findById(userId).orElseThrow(() ->{
            log.warn("[JourneyService] 유저 조회 실패 - userId: {}", userId);
            return null; // TODO: UserException - USER_NOT_FOUND 만들어지면 대체
        });

        // 여행 리스트 조회
        List<Journey> journeyList = journeyRepository.findAllByUser(user).orElseThrow(() -> {
            log.warn("[JourneyService] 여행 조회 실패"); // TODO: 제대로 작성
            return new CustomException(JourneyErrorCode.JOURNEY_NOT_FOUND);
        });

        // 응답 DTO들을 담을 리스트 선언
        List<JourneyResponse> responseList = new ArrayList<>();

        // 리스트 순회하며 DTO 변환 -> add
        for(Journey journey : journeyList) {
            responseList.add(toJourneyResponse(journey));
        }

        // DTO 리스트 반환
        return responseList;

    }

    public List<JourneyMapPinResponse> getMapPins(Long userId){

        log.info("[JourneyService] 지도 핀 조회 - 시작");

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("[JourneyService] 유저를 찾을 수 없습니다. - userId : {}", userId);
            return null; // TODO: UserException 생기면 제대로 구현
        });

        // 여행 리스트 불러오기
        List<Journey> journeyList = journeyRepository.findAllByUser(user).orElseThrow(() -> {
            log.warn("[JourneyService] 여행을 찾을 수 없습니다.");
            return new CustomException(JourneyErrorCode.JOURNEY_NOT_FOUND);
        }); // TODO 제대로 구현

        // 반환용 리스트 생성
        List<JourneyMapPinResponse> responseList = new ArrayList<>();

        // 순회하며 변환
        for(Journey journey : journeyList) {
            responseList.add(toJourneyMapPinResponse(journey));
        }

        return responseList;
    }

    // DTO 변환 메소드
    private JourneyResponse toJourneyResponse(Journey journey){
        return JourneyResponse.builder()
                .journeyId(journey.getId())
                .nationKRName(journey.getNation().getKrName())
                .nationENName(journey.getNation().getEnName())
                .type(journey.getType())
                .coverImgUrl(journey.getCoverImgUrl())
                .startDate(journey.getStartDate())
                .endDate(journey.getEndDate())
                .flagImgUrl(journey.getNation().getImgUrl())
                .build();
    }

    // 맵 핀 DTO 변환 메소드
    private JourneyMapPinResponse toJourneyMapPinResponse(Journey journey){
        return JourneyMapPinResponse
                .builder()
                .journeyId(journey.getId())
                .longitude(journey.getLongitude())
                .latitude(journey.getLatitude())
                .build();
    }
}
