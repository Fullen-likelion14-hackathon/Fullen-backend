package com.erbe.erbebackend.domain.post.service;

import com.erbe.erbebackend.domain.journey.entity.Journey;
import com.erbe.erbebackend.domain.journey.exception.JourneyErrorCode;
import com.erbe.erbebackend.domain.journey.repository.JourneyRepository;
import com.erbe.erbebackend.domain.photo.entity.Photo;
import com.erbe.erbebackend.domain.photo.repository.PhotoRepository;
import com.erbe.erbebackend.domain.post.dto.request.PostCreateRequest;
import com.erbe.erbebackend.domain.post.dto.response.PostResponse;
import com.erbe.erbebackend.domain.post.entity.Post;
import com.erbe.erbebackend.domain.post.repository.PostRepository;
import com.erbe.erbebackend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final PhotoRepository photoRepository;
    private final JourneyRepository journeyRepository;

    public PostResponse createPost(PostCreateRequest request, Long journeyId, Long userId) {

        // imgURLList
        List<String> imgUrlList = request.getImgUrlList();

        Journey journey = journeyRepository.findById(journeyId).orElseThrow(() ->{
            log.warn("[PostService] 여행 조회 실패 - journeyId: {}", journeyId);
            return new CustomException(JourneyErrorCode.JOURNEY_NOT_FOUND);
        });

        // 첫 게시물인지 판단
        Boolean isExist = postRepository.existsByJourney(journey);

        String firstImgURL = imgUrlList.get(0);

        /*
        첫 게시물이라면
        게시물의 첫 이미지로 여행의 대표 이미지 변경
        +
        그 이미지의 위도 경도를 추출하여, Journey의 위도 경도 값 변경 -> Journey 맵 핀 위치가 첫 사진을 찍은 장소로 변경됨
         */
        if(!isExist){
            // TODO 메타데이터 추출해서 Journey 위도 경도 값 변경 -> 현재 메타데이터 추출 관련 코드 없으므로 우선순위 미루기
            journey.updateFirstImageUrl(firstImgURL);
        }

        // Post 빌더로 생성
        Post post = Post.builder()
                .createdDate(LocalDate.now())
                .comment(request.getComment())
                .isPublic(request.getIsPublic())
                .imgUrl(firstImgURL)
                .nation(journey.getNation())
                .journey(journey)
                .build();

        // 게시물 저장
        Post savedPost = postRepository.save(post);

        // imgUrlList에 대해 순회를 돌며 사진 저장
        for(int i = 0; i < imgUrlList.size(); i++){
            Photo photo = Photo.builder()
                    .seq(i)
                    .imgUrl(imgUrlList.get(i))
                    .post(savedPost)
                    .build();

            photoRepository.save(photo);
        }

        // PostResponse 변환
        PostResponse response = toPostResponse(savedPost);

        // DTO 반환
        return response;
    }

    private PostResponse toPostResponse(Post post){

        // DTO에 들어갈 Photo URL 리스트 생성
        List<String> imgUrlList = new ArrayList<>();

        // 해당 게시물의 Photo 전부 가져오기
        List<Photo> photoList = photoRepository.findAllByPost(post);

        // Photo 리스트 순회돌며 이미지 URL add
        for(Photo photo : photoList){
            imgUrlList.add(photo.getImgUrl());
        }

        // 빌더로 response 생성 후 반환
        PostResponse response = PostResponse.builder()
                .postId(post.getId())
                .nationKRName(post.getNation().getKrName())
                .journeyType(post.getJourney().getType())
                .date(post.getCreatedDate())
                .imgUrlList(imgUrlList)
                .comment(post.getComment())
                .build();

        return response;
    }
}
