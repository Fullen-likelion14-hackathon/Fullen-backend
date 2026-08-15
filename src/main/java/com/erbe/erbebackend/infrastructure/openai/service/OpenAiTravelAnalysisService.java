package com.erbe.erbebackend.infrastructure.openai.service;

import com.erbe.erbebackend.domain.journey.entity.Journey;
import com.erbe.erbebackend.domain.journey.exception.JourneyErrorCode;
import com.erbe.erbebackend.domain.journey.repository.JourneyRepository;
import com.erbe.erbebackend.domain.post.entity.Post;
import com.erbe.erbebackend.domain.post.repository.PostRepository;
import com.erbe.erbebackend.domain.user.entity.User;
import com.erbe.erbebackend.domain.user.exception.UserErrorCode;
import com.erbe.erbebackend.domain.user.repository.UserRepository;
import com.erbe.erbebackend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAiTravelAnalysisService {

    private final JourneyRepository journeyRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    private final ChatClient chatClient;

    public String getResult(Long userId) {

        log.info("[OpenAiTravelAnalysisService] 여행 스타일 분석 - 시작 : userId = {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("[OpenAiTravelAnalysisService] user not found : userId = {}", userId);
            return new CustomException(UserErrorCode.USER_NOT_FOUND);
        });

        String systemPrompt = """
            [System Role]
                   너는 MCM 브랜드의 럭셔리 & 트래블 라이프스타일 에디터이자 전문 여행 분석가야.
                   사용자가 MCM 가방과 함께 다녀온 여행 기록(텍스트, 사진 묘사/태그, 여행지 정보)을 분석하여, 사용자의 감성적/행동적 여행 스타일을 정교하게 도출하는 것이 너의 역할이야.
            
            [Instruction & Rules]
                   1. 입력으로 주어지는 여행 정보(게시물 내용, 사진 태그/설명, 가방 정보, 방문 장소)를 기반으로 여행 스타일을 분석해줘.
                   2. 분석 결과는 MCM 브랜드가 지향하는 트렌디하고 감각적인 톤앤매너(무드)를 반영해서 작성해줘.
                   3. 억지로 추측하지 말고, 주어진 데이터에서 드러나는 사실과 분위기에 기반해 분석해줘.
                   4. 분석 결과는 서비스에서 바로 DB나 프론트엔드에 처리할 수 있도록 반드시 'JSON' 형식으로만 응답해.
            
            [Input Data]
                   - 사용자가 등록한 MCM 가방 정보: {bag_info}
                   - 여행 제목 및 일정: {trip_title}, {trip_duration}
                   - 작성한 게시물 텍스트: {post_content}
                   - 여행 사진 데이터/키워드: {photo_descriptions}
                   - 방문한 주요 장소/카테고리: {visited_places}
            
            [Output JSON Format]
                   {
                     "travel_style_type": "한 줄로 표현하는 여행 스타일 타이틀 (예: 'MCM Stark 백팩과 함께하는 감각적 예술 탐험가')",
                     "style_keywords": ["키워드1", "키워드2", "키워드3"],
                     "summary": "전체적인 여행 스타일 요약 및 분위기 설명 (3~4문장)",
                     "mcm_match_reason": "사용자가 들고 다닌 MCM 가방이 이 여행 스타일과 어떻게 어울렸는지에 대한 브랜드 감성적 해석 (1~2문장)",
                     "scores": {
                       "activity_level": 0-100 사이 숫자 (활동성: 휴양 vs 액티비티),
                       "planning_level": 0-100 사이 숫자 (계획성: 즉흥 vs 계획),
                       "photo_vibe": 0-100 사이 숫자 (감성: 힐링/풍경 vs 트렌디/인스타그래머블)
                     },
                     "recommended_next_destination": "이 스타일을 가진 사용자에게 추천하는 다음 MCM 여행 스팟 1곳과 이유"
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

        String finalAdditionalInformation = additionalInformation;

        // 여행 분석 요청
        String result = chatClient.prompt()
                .user(promptUserSpec ->
                        promptUserSpec
                                .text(finalAdditionalInformation)
                )
                .system(promptSystemSpec ->
                        promptSystemSpec
                                .text(systemPrompt))
                .call().content();

        log.info("[OpenAiTravelAnalysisService] 유저 여행 분석 - 종료 : result = {}", result);

        return result;
    }
}
