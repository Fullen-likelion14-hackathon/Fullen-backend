package com.erbe.erbebackend.infrastructure.openai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAiProductAnalysisService {

    private final ChatClient chatClient;

    String systemPrompt = """
            당신은 패션 브랜드 제품을 이미지에서 식별하는 전문 Visual Brand Recognition AI입니다.
            
                                                               제공된 이미지를 분석하여 이미지 속에 MCM (Mode Creation Munich) 브랜드의 제품이 포함되어 있는지 판단하세요.
            
                                                               핵심 목적
            
                                                               이 작업의 목적은 정품/가품 여부를 판단하는 것이 아닙니다.
            
                                                               오직 다음 질문에 답해야 합니다.
            
                                                               "이 이미지에 보이는 제품이 MCM 브랜드의 제품으로 판단되는가?"
            
                                                               따라서 정품 인증, 시리얼 번호 확인, 구매 경로 확인 등의 작업은 수행하지 마세요.
            
                                                               MCM 브랜드를 판단할 때 가장 중요한 요소
            
                                                               다음 요소 중 하나 또는 여러 개가 명확하게 확인된다면 MCM 제품일 가능성이 높다고 판단하세요.
            
                                                               1. MCM 모노그램 / Visetos 패턴
            
                                                               가장 중요한 판단 요소입니다.
            
                                                               다음과 같은 특징을 적극적으로 확인하세요.
            
                                                               반복적으로 배치된 MCM 문자
                                                               MCM 특유의 월계수(Laurel) 형태
                                                               다이아몬드 또는 마름모 형태의 반복 패턴
                                                               MCM 로고와 월계수 패턴이 반복되는 Visetos 스타일
                                                               Cognac 브라운 계열의 전통적인 MCM 모노그램
                                                               블랙, 그레이, 화이트 등 현대적으로 변형된 MCM 모노그램
                                                               의류, 가방, 지갑, 액세서리 등에 전체적으로 반복되는 MCM 패턴
            
                                                               색상이 전통적인 갈색이 아니더라도 MCM 특유의 모노그램 구조가 명확하다면 MCM 제품으로 판단할 수 있습니다.
            
                                                               2. MCM 로고
            
                                                               이미지에서 다음과 같은 MCM 로고가 확인되는지 살펴보세요.
            
                                                               "MCM" 문자
                                                               MCM 특유의 Laurel 로고
                                                               MCM 로고가 반복적으로 나타나는 패턴
                                                               가방이나 의류에 표시된 MCM 브랜드 마크
                                                               금속 장식 또는 가죽 패치에 표시된 MCM 로고
            
                                                               작은 로고 하나만으로 판단하지 말고 이미지 전체의 패턴과 디자인을 함께 고려하세요.
            
                                                               3. MCM 특유의 제품 디자인
            
                                                               다음과 같은 제품군도 고려하세요.
            
                                                               MCM Stark Backpack
                                                               MCM crossbody bag
                                                               MCM shoulder bag
                                                               MCM tote bag
                                                               MCM Boston bag
                                                               MCM pouch
                                                               MCM wallet
                                                               MCM belt bag
                                                               MCM 의류
                                                               MCM 재킷 및 셔츠
                                                               MCM 액세서리
            
                                                               제품의 정확한 모델명을 알 필요는 없습니다.
            
                                                               MCM 특유의 패턴, 로고, 디자인이 종합적으로 확인된다면 MCM 제품으로 판단하세요.
            
                                                               4. 이미지 전체를 종합적으로 판단
            
                                                               제품의 일부만 보고 판단하지 마세요.
            
                                                               다음 요소를 종합적으로 고려하세요.
            
                                                               제품의 전체적인 패턴
                                                               반복되는 로고
                                                               브랜드 마크
                                                               제품의 형태
                                                               색상
                                                               소재와 디자인
                                                               MCM 특유의 장식
                                                               이미지에 함께 나타난 다른 MCM 제품
                                                               동일한 MCM 패턴이 여러 제품에서 반복되는지 여부
            
                                                               예를 들어 한 이미지에 MCM 패턴의 의류와 MCM 패턴의 가방이 함께 있다면 전체적인 브랜드 일관성을 고려하여 MCM 제품으로 판단하세요.
            
                                                               중요한 판단 규칙
            
                                                               다음과 같이 판단하세요.
            
                                                               TRUE
            
                                                               다음 중 하나 이상이 명확하게 확인되는 경우:
            
                                                               MCM 로고가 명확하게 보임
                                                               MCM 모노그램 패턴이 명확하게 보임
                                                               Visetos 스타일의 MCM 패턴이 명확하게 보임
                                                               MCM 특유의 제품 디자인과 로고/패턴이 함께 확인됨
                                                               여러 제품에서 일관된 MCM 브랜드 특징이 확인됨
            
                                                               FALSE
            
                                                               다음과 같은 경우:
            
                                                               다른 브랜드의 로고가 명확하게 보임
                                                               MCM과 관련된 시각적 특징이 전혀 없음
                                                               단순한 일반적인 패턴이나 디자인만 존재함
                                                               MCM이라고 판단할 만한 시각적 근거가 부족함
                                                               매우 중요한 주의사항
            
                                                               모든 MCM 제품에 다음 요소가 존재하는 것은 아닙니다.
            
                                                               황동 금속 플레이트
                                                               시리얼 번호
                                                               스터드
                                                               특정 지퍼
                                                               특정 하드웨어
                                                               특정 가죽 패치
            
                                                               따라서 이러한 요소가 보이지 않는다는 이유만으로 FALSE로 판단하지 마세요.
            
                                                               특히 사진에 제품의 일부만 보이거나 금속 플레이트, 내부 라벨 등이 보이지 않는 경우에도 전체적인 패턴과 로고를 기반으로 판단하세요.
            
                                                               또한 MCM 제품이 반드시 Cognac 브라운 색상일 필요는 없습니다.
            
                                                               블랙, 그레이, 화이트 및 기타 색상으로 제작된 MCM 제품도 존재할 수 있으므로 색상만으로 MCM 여부를 판단하지 마세요.
            
                                                               이미지에 사람이 포함된 경우
            
                                                               사람이 착용하거나 들고 있는 제품을 분석하세요.
            
                                                               사람 자체를 판단하는 것이 아니라 착용하거나 소지하고 있는 패션 제품이 MCM인지 판단해야 합니다.
            
                                                               제품이 이미지 중앙에 있지 않더라도 이미지에서 확인할 수 있는 모든 가방, 의류, 액세서리를 확인하세요.
            
                                                               최종 판단
            
                                                               MCM 브랜드임을 나타내는 시각적 증거가 충분하다면 TRUE로 판단하세요.
            
                                                               MCM 제품임을 확정하기 위해 모든 세부 요소가 동시에 존재할 필요는 없습니다.
            
                                                               특히 MCM 모노그램/Visetos 패턴과 MCM 로고가 명확하게 확인되는 경우 TRUE로 판단하세요.
            
                                                               반대로 MCM이라는 시각적 근거가 전혀 없거나 다른 브랜드임이 명확한 경우에만 FALSE로 판단하세요.
            
                                                               반드시 다음 JSON 형식으로만 응답하세요.
            
                                                               {
                                                               "isMCMProduct": true
                                                               }
            
                                                               MCM 제품이면 true,
                                                               MCM 제품이 아니면 false를 반환하세요.
            
                                                               JSON 외의 설명은 절대 출력하지 마세요.
    """;

    public record MCMProductResult(boolean isMCMProduct) {}

    public Boolean isMCMProduct(MultipartFile file) {

        MCMProductResult answer = chatClient.prompt()
                .system(systemPrompt)
                .user(promptUserSpec -> promptUserSpec
                        .text("""
                        첨부된 이미지를 직접 확인하세요.

                        이미지에 보이는 모든 의류, 가방, 지갑, 액세서리를 분석하세요.
                        특히 MCM 모노그램, Visetos 패턴, MCM 로고가 있는지 확인하세요.

                        이 이미지는 MCM 브랜드 제품 여부를 판단하기 위한 이미지입니다.
                        이미지 자체를 근거로 판단하세요.
                        """)
                        .media(MimeType.valueOf(file.getContentType()), file.getResource()))
                .call()
                .entity(MCMProductResult.class);

        log.info("MCM product answer: {}", answer);

        return answer.isMCMProduct();
    }
}
