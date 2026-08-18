package com.erbe.erbebackend.domain.journey.service;

import com.erbe.erbebackend.domain.journey.dto.request.JourneyCreateRequest;
import com.erbe.erbebackend.domain.journey.dto.request.JourneyUpdateRequest;
import com.erbe.erbebackend.domain.journey.dto.response.*;
import com.erbe.erbebackend.domain.journey.entity.Journey;
import com.erbe.erbebackend.domain.journey.exception.JourneyErrorCode;
import com.erbe.erbebackend.domain.journey.repository.JourneyRepository;
import com.erbe.erbebackend.domain.nation.entity.Nation;
import com.erbe.erbebackend.domain.nation.enums.Continent;
import com.erbe.erbebackend.domain.nation.exception.NationErrorCode;
import com.erbe.erbebackend.domain.nation.repository.NationRepository;
import com.erbe.erbebackend.domain.post.entity.Post;
import com.erbe.erbebackend.domain.post.repository.PostRepository;
import com.erbe.erbebackend.domain.post.service.PostService;
import com.erbe.erbebackend.domain.user.entity.User;
import com.erbe.erbebackend.domain.user.exception.UserErrorCode;
import com.erbe.erbebackend.domain.user.repository.UserRepository;
import com.erbe.erbebackend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JourneyService {

    private final JourneyRepository journeyRepository;
    private final UserRepository userRepository;
    private final NationRepository nationRepository;
    private final PostRepository postRepository;
    private final PostService postService;

    public JourneyResponse findJourneyById(Long id, Long userId) {

        log.info("[JourneyService] 여행 단일 조회 - 시작");

        // DB에서 여행 조회
        Journey journey = journeyRepository.findById(id).orElseThrow(() -> {
            log.warn("[JourneyService] 여행 조회 실패 - 존재하지 않는 여행 journeyId: {}", id);
            return new CustomException(JourneyErrorCode.JOURNEY_NOT_FOUND);
        });

        // 본인의 여행이 아닌 다른 사람의 여행을 조회하려하면 X
        if(!(journey.getUser().getId().equals(userId))) {
            log.warn("[JourneyService] 여행 조회 실패 - 타인 여행 조회 시도 journeyId : {}, userId: {}", id, userId);
            throw new CustomException(JourneyErrorCode.NOT_JOURNEY_OWNER);
        }

        // DTO 변환
        JourneyResponse response = toJourneyResponse(journey);

        log.info("[JourneyService] 여행 단일 조회 - 종료 : journeyId = {}",  response.getJourneyId());

        // DTO 반환
        return response;
    }

    public JourneyByContinentResponse findAllJourneys(Long userId){

        log.info("[JourneyService] 모든 여행 조회 - 시작");

        // 1 유저 조회
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("[JourneyService] 유저 조회 실패 - userId: {}", userId);
            return new CustomException(UserErrorCode.USER_NOT_FOUND);
        });

        List<Journey> latestJourneys = new ArrayList<>();

        // 2 대륙별 가장 늦은 여행 찾아서 리스트에 추가
        for(Continent continent : Continent.values()) {

            // 각 대륙별, 최신 등록된 여행들만 가져오기
            Journey latestJourney = journeyRepository.findTopByUserAndNationContinentOrderByStartDateDesc(user, continent);

            // 여행이 존재할때만(남극 같은 대륙에 여행 가지 않았을 수도 있음) 리스트에 add해서 null 방지
            if(latestJourney != null) {
                latestJourneys.add(latestJourney);
            }

        }

        // 여행 시작일이 최신인 순서대로 여행 정렬
        List<Journey> sortedList = latestJourneys.stream()
                .sorted(Comparator.comparing(Journey::getStartDate).reversed())
                .toList();

        // 정렬 기준 대륙을 저장해둘 리스트 뽑기
        List<Continent> sortedContinents = new ArrayList<>();

        // 대륙 정렬 기준 추출
        for(Journey journey : sortedList){
            sortedContinents.add(journey.getNation().getContinent());
        }

        Map<String, ContinentJourneyGroupResponse> continetMap = new LinkedHashMap<>();

        // 정렬 기준으로 가져오기
        for(Continent continent : sortedContinents) {

            // 대륙별 리스트 가져오는 메소드
            List<Journey> tempJourneys = journeyRepository.findByUserAndNationContinentOrderByStartDateDesc(user, continent);

            if(tempJourneys.isEmpty()) continue;

            List<JourneyResponse> journeyResponses = new ArrayList<>();

            // 순회하며 DTO 변환
            for(Journey journey : tempJourneys){
                journeyResponses.add(toJourneyResponse(journey));
            }

            ContinentJourneyGroupResponse groupResponse = ContinentJourneyGroupResponse.builder()
                    .count(journeyResponses.size())
                    .journeys(journeyResponses)
                    .build();

            continetMap.put(continent.name(), groupResponse);
        }

        log.info("[JourneyService] 모든 여행 조회 - 종료: 여행 응답 개수 = {}", continetMap.size());

        JourneyByContinentResponse response = JourneyByContinentResponse.builder()
                .continents(continetMap)
                .build();

        return response;
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

    public JourneyResponse updateJourney(JourneyUpdateRequest request, Long journeyId, Long userId){

        log.info("[JourneyService] 여행 수정 시작 - journeyId : {}", journeyId);

        Journey journey = journeyRepository.findById(journeyId).orElseThrow(() -> {
           log.warn("[JourneyService] 여행을 찾을 수 없습니다 - journeyId : {}", journeyId);
           return new CustomException(JourneyErrorCode.JOURNEY_NOT_FOUND);
        });

        // 여행 무단 수정 시도시 차단
        if(!(journey.getUser().getId().equals(userId))){
            log.warn("[JourneyService] 여행 무단 수정 시도 - journeyId : {}, 무단 시도 userId : {}", journeyId, userId);
            throw new CustomException(JourneyErrorCode.NOT_JOURNEY_OWNER);
        }

        // 여행 업데이트
        journey.updateJourney(request.getImgUrl(), request.getType(), request.getStartDate(), request.getEndDate());

        return toJourneyResponse(journey);

    }

    public String deleteJourney(Long journeyId, Long userId){

        log.info("[JourneyService] 여행 삭제 시작 - journeyId : {}", journeyId);

        Journey journey = journeyRepository.findById(journeyId).orElseThrow(() -> {
            log.warn("[JourneyService] 여행을 찾을 수 없습니다 - journeyId : {}", journeyId);
            return new CustomException(JourneyErrorCode.JOURNEY_NOT_FOUND);
        });

        // 여행 무단 삭제 시도시 차단
        if(!(journey.getUser().getId().equals(userId))){
            log.warn("[JourneyService] 여행 무단 삭제 시도 - journeyId : {}, 무단 시도 userId : {}", journeyId, userId);
            throw new CustomException(JourneyErrorCode.NOT_JOURNEY_OWNER);
        }

        // 여행 게시물 목록 모두 가져오기
        List<Post> postList = postRepository.findAllByJourney(journey);

        // 게시물 하나하나 삭제(사진들 연관관계 존재하기에)
        for(Post post : postList){
            postService.deletePost(post.getId(), userId);
        }

        // 연관 관계 모두 해소했으므로, 여행 삭제
        journeyRepository.delete(journey);

        return "여행 삭제 성공 - journeyId : " + journeyId;

    }

    // 지도 내에서, 해당 핀의 여행과 가까운 2개의 여행을 찾아, 총 3개의 여행 정보를 반환해주는 로직
    public JourneyAtMapListResponse getJourneyWithNearbyJourneys(Long journeyId, Long userId) {

        log.info("[JourneyService] 가장 가까운 여행 찾기 - 시작 - journeyId = {}, userId = {}", journeyId, userId);

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("[JourneyService] 유저를 찾을 수 없습니다 - userId : {}", userId);
            return new CustomException(UserErrorCode.USER_NOT_FOUND);
        });

        // 1. 일단 Journey 가져오기
        Journey journey = journeyRepository.findById(journeyId).orElseThrow(() -> {
            log.warn("[JourneyService] 여행을 찾을 수 없습니다 - journeyId: {}", journeyId);
            return new CustomException(JourneyErrorCode.JOURNEY_NOT_FOUND);
        });

        // 해당 여행의 당사자가 아니라면 조회 불가능
        if(!journey.getUser().getId().equals(userId)) {
            log.warn("[JourneyService] 여행 무단 조회 시도 - journeyId = {}, 무단 시도 userId = {}", journeyId, userId);
            throw new CustomException(JourneyErrorCode.NOT_JOURNEY_OWNER);
        }

        // 서~동으로 경도에 따라 불러오며 인덱스 붙이기 + 같은 경도일경우, ID 기반으로 좌우 판별
        List<Journey> journeyList = journeyRepository.findAllByUserOrderByLongitudeAscIdAsc(user);

        // 여행 리스트의 총 사이즈
        int totalSize = journeyList.size();

        // 여행 리스트의 총 사이즈 == 1 -> 1개라면 가운데 여행만 표시하고, 양 옆은 표시 X
        if(totalSize == 1) {
            return JourneyAtMapListResponse.builder()
                    .leftJourney(null)
                    .centerJourney(toJourneyAtMapResponse(journey))
                    .rightJourney(null)
                    .build();
        }

        // 여행 리스트에서의 요청 ID를 가진 여행이 인덱스 몇 번에 위치해있는지
        int centerIndex = journeyList.indexOf(journey);

        // 순회하도록 설정해야하므로 % totalSize
        int leftIndex = (centerIndex - 1 + totalSize) % totalSize;
        int rightIndex = (centerIndex + 1) % totalSize;

        // 좌/우측 여행 가져오기
        Journey leftJourney = journeyList.get(leftIndex);
        Journey rightJourney = journeyList.get(rightIndex);

        // 반환용 DTO 변환
        JourneyAtMapListResponse responseList = JourneyAtMapListResponse.builder()
                .leftJourney(toJourneyAtMapResponse(leftJourney))
                .centerJourney(toJourneyAtMapResponse(journey))
                .rightJourney(toJourneyAtMapResponse(rightJourney))
                .build();

        // DTO 반환
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
                .postCount(journey.getPostCount())
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

    // 맵 내에서 확인 가능한 여행 정보 DTO 변환 메소드
    private JourneyAtMapResponse toJourneyAtMapResponse(Journey journey){
        return JourneyAtMapResponse.builder()
                .journeyId(journey.getId())
                .nationKRName(journey.getNation().getKrName())
                .type(journey.getType())
                .thumbnailUrl(journey.getFirstImgUrl())
                .startDate(journey.getStartDate())
                .endDate(journey.getEndDate())
                .postCount(journey.getPostCount())
                .flagImgUrl(journey.getNation().getImgUrl())
                .latitude(journey.getLatitude())
                .longitude(journey.getLongitude())
                .build();
    }
}
