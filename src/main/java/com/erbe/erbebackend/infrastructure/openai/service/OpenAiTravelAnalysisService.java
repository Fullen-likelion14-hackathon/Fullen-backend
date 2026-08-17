package com.erbe.erbebackend.infrastructure.openai.service;

import com.erbe.erbebackend.domain.artist.entity.Artist;
import com.erbe.erbebackend.domain.artist.repository.ArtistRepository;
import com.erbe.erbebackend.domain.journey.entity.Journey;
import com.erbe.erbebackend.domain.journey.exception.JourneyErrorCode;
import com.erbe.erbebackend.domain.journey.repository.JourneyRepository;
import com.erbe.erbebackend.domain.post.entity.Post;
import com.erbe.erbebackend.domain.post.repository.PostRepository;
import com.erbe.erbebackend.domain.user.entity.User;
import com.erbe.erbebackend.domain.user.exception.UserErrorCode;
import com.erbe.erbebackend.domain.user.repository.UserRepository;
import com.erbe.erbebackend.global.exception.CustomException;
import com.erbe.erbebackend.infrastructure.openai.dto.request.ReAnalysisRequest;
import com.erbe.erbebackend.infrastructure.openai.dto.response.AnalysisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAiTravelAnalysisService {

    private final JourneyRepository journeyRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    private final ChatClient chatClient;
    private final ArtistRepository artistRepository;

    public AnalysisResponse getResult(Long userId) {

        log.info("[OpenAiTravelAnalysisService] 여행 스타일 분석 - 시작 : userId = {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("[OpenAiTravelAnalysisService] user not found : userId = {}", userId);
            return new CustomException(UserErrorCode.USER_NOT_FOUND);
        });

        String systemPrompt = """
    [System Role]
           너는 MCM 브랜드의 럭셔리 & 트래블 라이프스타일 에디터이자 전문 여행 분석가야.
           사용자가 MCM 가방과 함께 다녀온 여행 기록과 [추천 가능 아티스트 목록]을 분석하여, 사용자의 여행 스타일을 정교하게 도출하고 가장 어울리는 아티스트 3명을 추천해주는 것이 너의 역할이야.
    
    [Instruction & Rules]
           1. 입력으로 주어지는 여행 정보(게시물 내용, 사진 태그/설명, 가방 정보, 방문 장소)를 기반으로 여행 스타일을 분석해줘.
           2. 분석 결과는 MCM 브랜드가 지향하는 트렌디하고 감각적인 톤앤매너를 반영해서 작성해줘.
           3. travelStyle: 영문 2~3단어로 구성된 명료하고 세련된 타이틀로 작성해줘 (예: Urban Minimalist, Scenic Romanticist).
           4. detail은 다음 구조로 작성해줘:
              - 첫 문장: "[핵심 취향/무드]를 좋아하는 당신을 분석했습니다"
              - 본문 문장: 사용자의 여행 성향 및 MCM과 함께하는 여행 방식을 표현한 2~3문장의 설명
           5. hashtagList: 여행 성향을 나타내는 짧은 태그 3~5개를 추출해줘 (예: ["도시", "건축", "야경", "새로운경험", "맛집탐방"]).
           6. artistIdList: [추천 가능 아티스트 목록] 중에서 사용자의 여행 스타일, 분위기, 감성과 가장 잘 어울리는 아티스트 3명의 'ID(Long 숫자)'를 선택하여 리스트로 반환해줘. (예: [1, 4, 7])
           7. 분석 결과는 서비스에서 바로 DB나 프론트엔드에 처리할 수 있도록 반드시 'JSON' 형식으로만 응답해야 해.
    
    [Input Data]
           - 사용자가 등록한 MCM 가방 정보: {bag_info}
           - 여행 제목 및 일정: {trip_title}, {trip_duration}
           - 작성한 게시물 텍스트: {post_content}
           - 여행 사진 데이터/키워드: {photo_descriptions}
           - 방문한 주요 장소/카테고리: {visited_places}
           - 추천 가능 아티스트 목록: {additionalInformation}
    
    [Output JSON Format]
           {
             "travelStyle": "Urban Minimalist",
             "detail": "도시의 감각과 건축, 미니멀한 무드를 좋아하는 당신을 분석했습니다\\n\\n유명 관광지보다 도시 곳곳을 천천히 탐색하며, 일상적인 순간을 기록하는 여행을 즐겨요.\\nMCM과 함께 도시를 자유롭게 탐험하는 여행자시군요.",
             "hashtagList": ["도시", "건축", "야경", "새로운경험", "맛집탐방"],
             "artistIdList": [1, 4, 7]
           }
    """;

        String additionalInformation = "";

        List<Journey> journeyList = journeyRepository.findAllByUser(user).orElseThrow(() -> {
            log.warn("[OpenAiTravelAnalysisService] 여행을 찾을 수 없습니다.");
            return new CustomException(JourneyErrorCode.JOURNEY_NOT_FOUND);
        });

        // 여행에 대해 반복을 돌며, 여행정보 + 게시물 정보 additionalInformation에 추가
        for(Journey journey : journeyList) {
            additionalInformation += "[Journey{" + journeyList.indexOf(journey) + "}{type =" + journey.getType() + "}" + "{" + "duration ="  + journey.getStartDate() + " ~ " + journey.getEndDate() + "}" + "{" + "nation = " + journey.getNation().getKrName()  + "}" + "=";

            List<Post> postList = postRepository.findAllByJourneyOrderByCreatedAtAsc(journey);

            for(int i = 0 ; i < postList.size() ; i++) {
                Post post =  postList.get(i);

                additionalInformation += "Post{" + i + "}" +  " : " + " comment={" + post.getComment() + "}; ";
            }

            additionalInformation += "]";
        }

        additionalInformation += " / ArtistList = { ";

        // 작가 모두 불러오기
        List<Artist> artistList = artistRepository.findAll();

        for(Artist artist : artistList) {
            additionalInformation += "artist{" + artist.getId() + "}" + " : " + "artistName={" + artist.getName() + "}" + "artistIntroSummary={"  + artist.getIntroSummary() + "};" + " , ";
        }

        String finalAdditionalInformation = additionalInformation;

        // 여행 분석 요청
        AnalysisResponse result = chatClient.prompt()
                .user(promptUserSpec ->
                        promptUserSpec
                                .text(finalAdditionalInformation)
                )
                .system(promptSystemSpec ->
                        promptSystemSpec
                                .text(systemPrompt))
                .call()
                        .entity(AnalysisResponse.class);

        result.setUsername(user.getName());

        log.info("[OpenAiTravelAnalysisService] 유저 여행 분석 - 종료 : result = {}", Objects.requireNonNull(result));

        return result;
    }

    public AnalysisResponse reAnalyze(ReAnalysisRequest request, Long userId){

        log.info("[OpenAiTravelAnalysisService] 여행 스타일 재분석 - 시작 : userId = {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("[OpenAiTravelAnalysisService] user not found : userId = {}", userId);
            return new CustomException(UserErrorCode.USER_NOT_FOUND);
        });

        String systemPrompt = """
    [System Role]
           너는 MCM 브랜드의 럭셔리 & 트래블 라이프스타일 에디터이자 전문 여행 분석가야.
           사용자가 기존 여행 분석 결과의 '상세 설명(detail)'을 직접 수정하여 재분석을 요청했어.
           너의 역할은 사용자가 수정한 텍스트를 최우선 기준(Ground Truth)으로 삼아, 사용자의 의도에 맞춰 여행 스타일, 해시태그, 그리고 가장 어울리는 아티스트 3명을 재추천하는 거야.
    
    [Instruction & Rules]
           1. 입력 데이터 중 [사용자가 수정/입력한 상세 설명]을 최우선 판단 기준으로 활용해.
           2. 사용자가 조정한 성향, 분위기, 행동 패턴을 반영하여 'travelStyle', 'detail', 'hashtagList', 'artistIdList'를 모두 새롭게 업데이트해줘.
           3. travelStyle: 수정된 성향을 가장 잘 나타내는 영문 2~3단어 타이틀로 새로 도출해줘 (예: Urban Minimalist, Scenic Romanticist).
           4. detail: 사용자가 수정한 핵심 의도를 살려 UI 톤앤매너에 맞게 깔끔한 3~4문장으로 다듬어줘.
              - 첫 문장은 반드시 "[핵심 취향/무드]를 좋아하는 당신을 분석했습니다" 형태를 유지할 것.
              - 사용자의 입력 문장이 거칠거나 짧더라도 MCM 럭셔리 트래블 에디터 톤앤매너로 자연스럽게 완성해줘.
           5. hashtagList: 수정된 상세 설명 및 입력 데이터에서 강조된 핵심 키워드 3~5개를 새로 추출해줘.
           6. artistIdList: 수정된 여행 스타일에 맞춰 [추천 가능 아티스트 목록] 중 가장 잘 어울리는 아티스트 3명의 'ID(Long 숫자)'를 새롭게 선택하여 리스트로 반환해줘.
           7. 분석 결과는 제시된 'JSON' 형식으로만 정확히 응답해야 해.
    
    [Input Data]
           - 사용자가 수정/입력한 상세 설명 (최우선 반영): {user_modified_detail}
           - 사용자가 등록한 MCM 가방 정보: {bag_info}
           - 여행 제목 및 일정: {trip_title}, {trip_duration}
           - 작성한 게시물 텍스트: {post_content}
           - 여행 사진 데이터/키워드: {photo_descriptions}
           - 방문한 주요 장소/카테고리: {visited_places}
           - 추천 가능 아티스트 목록: {additionalInformation}
    
    [Output JSON Format]
           {
             "travelStyle": "영문 2~3단어 스타일 타이틀",
             "detail": "[핵심 취향/무드]를 좋아하는 당신을 분석했습니다\\n\\n사용자 수정 의도가 반영된 자연스러운 설명 문장 1.\\nMCM과 함께하는 여행 방식을 담은 문장 2.",
             "hashtagList": ["키워드1", "키워드2", "키워드3"],
             "artistIdList": [1, 4, 7]
           }
    """;

        String additionalInformation = "";

        List<Journey> journeyList = journeyRepository.findAllByUser(user).orElseThrow(() -> {
            log.warn("[OpenAiTravelAnalysisService] 여행을 찾을 수 없습니다.");
            return new CustomException(JourneyErrorCode.JOURNEY_NOT_FOUND);
        });

        // 여행에 대해 반복을 돌며, 여행정보 + 게시물 정보 additionalInformation에 추가
        for(Journey journey : journeyList) {
            additionalInformation += "[Journey{" + journeyList.indexOf(journey) + "}{type =" + journey.getType() + "}" + "{" + "duration ="  + journey.getStartDate() + " ~ " + journey.getEndDate() + "}" + "{" + "nation = " + journey.getNation().getKrName()  + "}" + "=";

            List<Post> postList = postRepository.findAllByJourneyOrderByCreatedAtAsc(journey);

            for(int i = 0 ; i < postList.size() ; i++) {
                Post post =  postList.get(i);

                additionalInformation += "Post{" + i + "}" +  " : " + " comment={" + post.getComment() + "}; ";
            }

            additionalInformation += "]";
        }

        additionalInformation += " / ArtistList = { ";

        // 작가 모두 불러오기
        List<Artist> artistList = artistRepository.findAll();

        for(Artist artist : artistList) {
            additionalInformation += "artist{" + artist.getId() + "}" + " : " + "artistName={" + artist.getName() + "}" + "artistIntroSummary={"  + artist.getIntroSummary() + "};" + " , ";
        }

        String finalAdditionalInformation = additionalInformation;

        // 여행 분석 요청
        AnalysisResponse result = chatClient.prompt()
                .user(promptUserSpec ->
                        promptUserSpec
                                .text(finalAdditionalInformation)
                )
                .system(promptSystemSpec ->
                        promptSystemSpec
                                .text(systemPrompt))
                .call()
                .entity(AnalysisResponse.class);

        result.setUsername(user.getName());

        log.info("[OpenAiTravelAnalysisService] 유저 여행 재분석 - 종료 : result = {}", Objects.requireNonNull(result));

        return result;
    }
}
