package com.erbe.erbebackend.infrastructure.openai.service;

import com.erbe.erbebackend.domain.artist.entity.Artist;
import com.erbe.erbebackend.domain.artist.exception.ArtistErrorCode;
import com.erbe.erbebackend.domain.artist.repository.ArtistRepository;
import com.erbe.erbebackend.domain.photo.entity.Photo;
import com.erbe.erbebackend.domain.photo.exception.PhotoErrorCode;
import com.erbe.erbebackend.domain.photo.repository.PhotoRepository;
import com.erbe.erbebackend.global.exception.CustomException;
import com.erbe.erbebackend.global.s3.S3Uploader;
import com.erbe.erbebackend.global.s3.exception.S3ErrorCode;
import com.erbe.erbebackend.infrastructure.openai.dto.request.ImageGenRequest;
import com.erbe.erbebackend.infrastructure.openai.exception.OpenAiErrorCode;
import com.openai.client.OpenAIClient;
import com.openai.core.MultipartField;
import com.openai.models.images.Image;
import com.openai.models.images.ImageEditParams;
import com.openai.models.images.ImagesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAiImageGenService {

    private final PhotoRepository photoRepository;

    private final ChatClient chatClient;
    private final OpenAIClient openAIClient;
    private final S3Uploader s3Uploader;
    private final ArtistRepository artistRepository;

    public List<String> getResult(ImageGenRequest request, Long userId){

        log.info("[OpenAiImageGenService] 트래블 패치 생성 시작 - request : {}, userId : {}", request, userId);

        // 시스템 지시문
        String systemPrompt = """
    너는 Travel Patch용 이미지 편집 프롬프트를 작성하는 전문 Prompt Engineer다.

    사용자가 제공한 원본 여행 사진을 새로운 장면으로 재창작하지 말고,
    원본의 시각적 내용을 최대한 유지하면서 지정된 화풍과 Travel Patch 그래픽 스타일만 적용한다.

    ==================================================
    [CORE RULES]
    ==================================================

    1. ORIGINAL IMAGE PRESERVATION

    원본 이미지를 absolute source of truth로 사용한다.

    반드시 유지:
    - 주요 인물과 인물 수
    - 얼굴과 외형
    - 포즈와 자세
    - 의상과 액세서리
    - 주요 사물
    - 사물의 위치와 관계
    - 카메라 앵글과 시점
    - 전체적인 구도와 상대적 크기

    DO NOT:
    - create a new scene
    - add or remove people
    - change poses or identities
    - replace clothing or objects
    - rearrange the composition
    - invent new scenery, landmarks, buildings, vehicles or props

    The transformed image must remain immediately recognizable
    as the original photograph.


    2. STYLE TRANSFORMATION

    원본의 장면과 피사체는 유지하고 다음 요소만 변경한다:

    - artistic style
    - brushwork
    - color rendering
    - lighting interpretation
    - texture
    - material appearance
    - print treatment

    지정된 {artist_style}을 원본 장면에 적용한다.

    강한 핸드페인팅 및 고급 패션 액세서리용 그래픽 질감을 사용한다:

    thick brushstrokes, tactile painted texture,
    layered pigment, refined handmade finish,
    premium collectible graphic aesthetic.

    화풍은 피사체나 장면을 재창작하는 용도가 아니다.


    3. TRAVEL PATCH DESIGN

    지정된 {patch_type} 형태로 원본 장면을 Travel Patch로 구성한다.

    TICKET:
    rectangular vintage travel ticket with subtle perforation
    and compact archival typography.

    STAMP:
    postage stamp silhouette with serrated edges,
    vintage postal framing and subtle postmark details.

    LABEL:
    luggage tag silhouette with vintage travel-label framing
    and compact archival information.

    패치 외곽 형태만 변경하고 원본 장면의 내부 구도는 유지한다.


    4. TRAVEL METADATA

    다음 정보만 사용한다:

    - Nation: {travel_nation}
    - Type: {travel_type}
    - Date: {travel_date}

    메타데이터는 패치에 원래 인쇄되어 있던 것처럼
    자연스럽게 통합한다.

    printed, stamped, embossed, engraved,
    letterpressed 또는 archival typography처럼 표현한다.

    우선순위:

    original image > patch design > destination > travel type/date

    DO NOT invent:
    - city names
    - landmarks
    - airport codes
    - hotels
    - airlines
    - dates
    - destinations
    - slogans
    - unrelated text

    메타데이터는 얼굴이나 주요 피사체를 가리지 않는다.


    5. TRANSPARENT BACKGROUND — CRITICAL

    최종 결과물은 3D 가방 UV 텍스처에 바로 사용할 수 있는
    독립적인 2D Travel Patch 에셋이어야 한다.

    The area outside the patch silhouette MUST be fully transparent.

    반드시:
    - transparent background
    - transparent canvas outside the patch
    - alpha = 0 outside the patch silhouette
    - isolated patch asset

    절대 생성하지 않는다:
    - white background
    - off-white background
    - cream background
    - beige background
    - gray background
    - colored background
    - rectangular background
    - square background
    - table
    - wall
    - room
    - mockup
    - bag
    - hand holding the patch
    - surrounding environment
    - visible backdrop

    패치 자체의 흰색, 아이보리색, 크림색, 종이색,
    가죽색 등의 디자인은 허용한다.

    단, 이러한 색상은 반드시 패치 실루엣 내부에만 존재해야 한다.

    DO NOT create any non-transparent pixels outside
    the Travel Patch silhouette.


    ==================================================
    [IMAGE EDITING INSTRUCTION]
    ==================================================

    generated_image_prompt는 반드시 image editing 관점의
    실행 가능한 영문 프롬프트로 작성한다.

    반드시 다음 내용을 포함한다:

    - use the uploaded image as the exact visual source
    - preserve the original subjects and composition
    - apply the requested artistic style
    - transform it into the requested Travel Patch type
    - integrate the supplied travel metadata
    - keep the patch outside area fully transparent
    - do not create a white or colored background
    - do not invent new objects or scenery


    ==================================================
    [INPUT]
    ==================================================

    Patch Type: {patch_type}
    Artist / Style: {artist_style}
    Style Keywords: {style_keywords}
    Travel Nation: {travel_nation}
    Travel Type: {travel_type}
    Travel Date: {travel_date}


    ==================================================
    [OUTPUT]
    ==================================================

    {
      "patch_type": "TICKET | STAMP | LABEL",
      "applied_artist_style": "적용된 화가/화풍 이름",
      "travel_metadata": {
        "travel_nation": "여행 국가",
        "travel_type": "여행 유형",
        "travel_date": "여행 일자"
      },
      "generated_image_prompt": "최종 이미지 편집용 영문 프롬프트",
      "design_concept_summary": "디자인 컨셉과 핵심 특징을 설명하는 한글 1-2문장"
    }

    반드시 순수 JSON만 반환한다.
    Markdown code block이나 추가 설명은 반환하지 않는다.
    """;;

        // 사진 가져오기
        Photo photo = photoRepository.findById(request.getPhotoId()).orElseThrow(() -> {
            log.warn("[OpenAiTravelImageGenService] photo id={} not found", request.getPhotoId());
            return new CustomException(PhotoErrorCode.PHOTO_NOT_FOUND);
        });

        // 만약 사진이 유저 소유가 아니라면
        if(!(photo.getPost().getUser().getId().equals(userId))){
            log.warn("[OpenAiTravelImageGenService] 사진 무단 조회 시도 - photoId : {}, 무단 조회 시도 userId : {}", photo.getId(), userId);
            throw new CustomException(PhotoErrorCode.PHOTO_ACCESS_DENIED);
        }

        // 사진 정보 추출
        String photoDescription = "여행 국가 : " + photo.getPost().getJourney().getNation().getEnName() + ", 여행 유형 : " + photo.getPost().getJourney().getType() + ", 여행 일자 : " + String.valueOf(photo.getPost().getCreatedDate());

        Artist artist = artistRepository.findById(request.getArtistId()).orElseThrow(() -> {
            log.warn("[OpenAiImageGenService] 존재하지 않는 작가 ID입니다 - artistId : {}", request.getArtistId());
            throw new CustomException(ArtistErrorCode.ARTIST_NOT_FOUND);
        });

        // 사진 정보 + 작가 정보 userPrompt에 추가
        String userPrompt = request.getMessage() + " / 사진 설명 : " + photoDescription + " / 작가 설명 : {artistName}=" + artist.getName() + ", {artistDescription}=" + artist.getDescription();

        // 기존 s3에 저장되어있던 사진 URL 추출
        String imgURL = photo.getImgUrl();

        // mimetype 추출
        MimeType mimeType = imgURL.endsWith(".png") ? MimeTypeUtils.IMAGE_PNG : MimeTypeUtils.IMAGE_JPEG;

        // 완성 된 이미지 프롬프트 저장할 문자열
        String imagePrompt;

        try {
            URL imageResourceUrl = URI.create(imgURL).toURL();

            // 이미지 프롬프트 제작
            imagePrompt = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userSpec -> userSpec
                            .text(userPrompt)
                            .media(mimeType, imageResourceUrl)
                    )
                    .call()
                    .content();
        } catch (MalformedURLException e) {
            log.error("잘못된 S3 이미지 URL 형식입니다: {}", imgURL, e);
            throw new IllegalArgumentException("유효하지 않은 이미지 URL입니다.", e);
        }

        // 트래블 패치 저장을 위한 변수
        ImagesResponse response;

        // s3에서 사진 가져오기
        URL url;
        try {
            url = URI.create(imgURL).toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        URLConnection connection;
        try {
            connection = url.openConnection();
            connection.setConnectTimeout(5_000);
            connection.setReadTimeout(10_000);
            connection.connect();
        } catch (IOException e) {
            throw new CustomException(S3ErrorCode.S3_NOT_FOUND);
        }

        // mimetype 추출
        String contentType = connection.getContentType();

        if (contentType == null) {
            throw new IllegalArgumentException("이미지 Content-Type을 확인할 수 없습니다.");
        }

        contentType = contentType.split(";")[0].trim().toLowerCase();

        String filename = switch (contentType) {
            case "image/png" -> "input.png";
            case "image/jpeg" -> "input.jpg";
            case "image/webp" -> "input.webp";
            default -> throw new IllegalArgumentException(
                    "지원하지 않는 이미지 MIME type: " + contentType
            );
        };

        // InputStream -> MultipartFiled 변환 + OpenAIClient로 이미지 편집 호출
        try (InputStream inputStream = connection.getInputStream()) {

            ImageEditParams.Image image =
                    ImageEditParams.Image.ofInputStream(inputStream);

            MultipartField<ImageEditParams.Image> imageFile =
                    MultipartField.<ImageEditParams.Image>builder()
                            .value(image)
                            .filename(filename)
                            .contentType(contentType)
                            .build();

            // 기존 이미지 + 이미지 편집 프롬프트 + 모델 지정 + 3장 요청
            ImageEditParams params = ImageEditParams.builder()
                    .image(imageFile)
                    .prompt(imagePrompt)
                    .model("gpt-image-2")
                    .n(3L)
                    .build();

            response = openAIClient.images().edit(params);
        } catch (Exception e) {
            log.error("[OpenAiTravelImageGenService] 이미지 생성 실패 - e.message : {}", e.getMessage());
            throw new CustomException(OpenAiErrorCode.OPENAI_IMAGE_GEN_FAILED);
        }

        // 응답 결과 Image 리스트 가져오기
        List<Image> images = response.data().orElseThrow(() -> new IllegalStateException("OpenAI 이미지 편집 응답에 데이터가 없습니다."));

        // base64 인코딩 되어있는 이미지 담을 리스트
        List<String> imgUrlList = new ArrayList<>();

        for(Image image : images){
            String b64String = image._b64Json().asString().orElseThrow(() -> new IllegalStateException("OpenAI 응답에 b64_json 값이 없습니다."));

            String s3URL = s3Uploader.uploadBase64ImageToS3(b64String);

            imgUrlList.add(s3URL);
        }

        log.info("[OpenAiImageGenService] 트래블패치 제작 성공 : imgUrlList : {}", imgUrlList);

        return imgUrlList;

    }
}
