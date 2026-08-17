package com.erbe.erbebackend.infrastructure.openai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "ImageGenResponse: 트래블패치 생성 응답 DTO")
public class ImageGenResponse {
    private List<String> answer;

    public static ImageGenResponse of(List<String> answer){
        return new ImageGenResponse(answer);
    }
}
