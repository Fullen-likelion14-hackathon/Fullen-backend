package com.erbe.erbebackend.domain.journey.service;

import com.erbe.erbebackend.domain.journey.dto.request.JourneyCreateRequest;
import com.erbe.erbebackend.domain.journey.dto.response.JourneyMapPinResponse;
import com.erbe.erbebackend.domain.journey.dto.response.JourneyResponse;
import com.erbe.erbebackend.domain.journey.entity.Journey;
import com.erbe.erbebackend.domain.journey.exception.JourneyErrorCode;
import com.erbe.erbebackend.domain.journey.repository.JourneyRepository;
import com.erbe.erbebackend.domain.nation.entity.Nation;
import com.erbe.erbebackend.domain.nation.exception.NationErrorCode;
import com.erbe.erbebackend.domain.nation.repository.NationRepository;
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
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JourneyService {

    private final JourneyRepository journeyRepository;
    private final UserRepository userRepository;
    private final NationRepository nationRepository;

    public JourneyResponse findJourneyById(Long id, Long userId) {

        log.info("[JourneyService] 여행 단일 조회 - 시작");

        // DB에서 여행 조회
        Journey journey = journeyRepository.findById(id).orElseThrow(() -> {
            log.warn("[JourneyService] 여행 조회 실패 - 존재하지 않는 여행 journeyId: {}", id);
            return new CustomException(JourneyErrorCode.JOURNEY_NOT_FOUND);
        });

        // 본인의 여행이 아닌 다른 사람의 여행을 조회하려하면 X
        if(Objects.equals(journey.getUser().getId(), userId)) {
            log.warn("[JourneyService] 여행 조회 실패 - 타인 여행 조회 시도 journeyId : {}, userId: {}", id, userId);
            throw new CustomException(JourneyErrorCode.NOT_JOURNEY_OWNER);
        }

        // DTO 변환
        JourneyResponse response = toJourneyResponse(journey);

        log.info("[JourneyService] 여행 단일 조회 - 종료 : journeyId = {}",  response.getJourneyId());

        // DTO 반환
        return response;
    }

    public List<JourneyResponse> findAllJourneys(Long userId) {

        log.info("[JourneyService] 모든 여행 조회 - 시작");

        // 유저 조회
        User user = userRepository.findById(userId).orElseThrow(() ->{
            log.warn("[JourneyService] 유저 조회 실패 - userId: {}", userId);
            return new CustomException(UserErrorCode.USER_NOT_FOUND);
        });

        // 여행 리스트 조회
        List<Journey> journeyList = journeyRepository.findAllByUser(user).orElseThrow(() -> {
            log.warn("[JourneyService] 여행 조회 실패");
            return new CustomException(JourneyErrorCode.JOURNEY_NOT_FOUND);
        });

        // 응답 DTO들을 담을 리스트 선언
        List<JourneyResponse> responseList = new ArrayList<>();

        // 리스트 순회하며 DTO 변환 -> add
        for(Journey journey : journeyList) {
            responseList.add(toJourneyResponse(journey));
        }

        log.info("[JourneyService] 모든 여행 조회 - 종료: 여행 응답 개수 = {}", responseList.size());

        // DTO 리스트 반환
        return responseList;

    }

    public List<JourneyMapPinResponse> getMapPins(Long userId){

        log.info("[JourneyService] 지도 핀 조회 - 시작");

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("[JourneyService] 유저를 찾을 수 없습니다. - userId : {}", userId);
            return new CustomException(UserErrorCode.USER_NOT_FOUND);
        });

        // 여행 리스트 불러오기
        List<Journey> journeyList = journeyRepository.findAllByUser(user).orElseThrow(() -> {
            log.warn("[JourneyService] 여행을 찾을 수 없습니다.");
            return new CustomException(JourneyErrorCode.JOURNEY_NOT_FOUND);
        });

        // 반환용 리스트 생성
        List<JourneyMapPinResponse> responseList = new ArrayList<>();

        // 순회하며 변환
        for(Journey journey : journeyList) {
            responseList.add(toJourneyMapPinResponse(journey));
        }

        return responseList;
    }

    public JourneyResponse createJourney(JourneyCreateRequest request, Long userId){

        log.info("[JourneyService] 여행 생성 - 시작");

        // 나라 찾기
        Nation nation = nationRepository.findByKrName(request.getNationName()).orElseThrow(() ->{
            log.warn("[JourneyService] 나라 조회 실패 - nationName : {}", request.getNationName());
            return new CustomException(NationErrorCode.NATION_NOT_FOUND);
        });

        // 유저 찾기
        User user = userRepository.findById(userId).orElseThrow(() ->{
            log.warn("[JourneyService] 유저 조회 실패 - userId : {}", userId);
            return new CustomException(UserErrorCode.USER_NOT_FOUND);
        });

        /*
        새 여행 생성
        위도 경도는 최초 생성 시, 그 나라의 대표 위도 경도값을 삽입
        나중에, 1번째 포스트가 생성되면 업데이트 되는 구조
         */
        Journey journey = Journey.builder()
                .type(request.getType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .coverImgUrl(request.getImgUrl())
                .nation(nation)
                .user(user)
                .longitude(nation.getLongitude())
                .latitude(nation.getLatitude())
                .build();

        // 새 여행 DB에 저장
        Journey savedJourney = journeyRepository.save(journey);

        // 저장된 여행 dto로 변환
        JourneyResponse response = toJourneyResponse(savedJourney);

        // dto 반환
        return response;

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
