package com.erbe.erbebackend.domain.post.service;

import com.erbe.erbebackend.domain.journey.entity.Journey;
import com.erbe.erbebackend.domain.journey.exception.JourneyErrorCode;
import com.erbe.erbebackend.domain.journey.repository.JourneyRepository;
import com.erbe.erbebackend.domain.nation.entity.Nation;
import com.erbe.erbebackend.domain.nation.exception.NationErrorCode;
import com.erbe.erbebackend.domain.nation.repository.NationRepository;
import com.erbe.erbebackend.domain.photo.dto.response.PhotoResponse;
import com.erbe.erbebackend.domain.photo.entity.Photo;
import com.erbe.erbebackend.domain.photo.repository.PhotoRepository;
import com.erbe.erbebackend.domain.post.dto.request.PostCreateRequest;
import com.erbe.erbebackend.domain.post.dto.request.PostUpdateRequest;
import com.erbe.erbebackend.domain.post.dto.response.PostCardResponse;
import com.erbe.erbebackend.domain.post.dto.response.PostPreviewResponse;
import com.erbe.erbebackend.domain.post.dto.response.PostResponse;
import com.erbe.erbebackend.domain.post.entity.Post;
import com.erbe.erbebackend.domain.post.exception.PostErrorCode;
import com.erbe.erbebackend.domain.post.repository.PostRepository;
import com.erbe.erbebackend.domain.user.entity.User;
import com.erbe.erbebackend.domain.user.exception.UserErrorCode;
import com.erbe.erbebackend.domain.user.repository.UserRepository;
import com.erbe.erbebackend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final PhotoRepository photoRepository;
    private final JourneyRepository journeyRepository;
    private final UserRepository userRepository;
    private final NationRepository nationRepository;

    // 게시글 작성 로직
    public PostResponse createPost(PostCreateRequest request, Long journeyId, Long userId) {

        log.info("[PostService] 게시글 작성 - 시작");

        // imgURLList
        List<String> imgUrlList = request.getImgUrlList();

        // 유저 유효성 검사 - jwt 토큰으로 가능하지만 혹시 모르니 예외처리 추가
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("[PostService] 유저를 찾을 수 없습니다 - userId = {}", userId);
            return new CustomException(UserErrorCode.USER_NOT_FOUND);
        });

        Journey journey = journeyRepository.findById(journeyId).orElseThrow(() ->{
            log.warn("[PostService] 여행 조회 실패 - journeyId: {}", journeyId);
            return new CustomException(JourneyErrorCode.JOURNEY_NOT_FOUND);
        });

        if(!journey.getUser().getId().equals(userId)){
            log.warn("[PostService] 타인 여행 게시물 작성 시도 - journeyId: {}, 시도 한 userId: {}", journeyId, userId);
            throw new CustomException(JourneyErrorCode.NOT_JOURNEY_OWNER);
        }

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
            if(request.getLongitude() != null && request.getLatitude() != null){
                journey.updatePos(request.getLongitude(), request.getLatitude());
            }
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
                .user(user)
                .photoCount(imgUrlList.size())
                .build();

        // 게시물 저장
        Post savedPost = postRepository.save(post);

        // Journey의 postCount 증가
        journeyRepository.incrementPostCount(journeyId);

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

        log.info("[PostService] 게시글 작성 - 종료 : postId={}", savedPost.getId());

        // DTO 반환
        return response;
    }

    // 게시글 조회 로직
    public PostResponse getPost(Long postId, Long userId){

        log.info("[PostService] 게시글 조회 시작 - postId={}, userId={}", postId, userId);

        // 포스트 엔티티 가져오기
        Post post = postRepository.findById(postId).orElseThrow(() ->{
            log.warn("[PostService] 게시글을 찾을 수 없습니다. - postId: {}", postId);
            return new CustomException(PostErrorCode.POST_NOT_FOUND);
        });

        // 만약 Post의 isPublic이 false(비공개 게시물이라면) -> 게시물 주인인지 검증하여야함
        if(!post.getIsPublic()){
            if(!(post.getJourney().getUser().getId().equals(userId))){
                log.warn("[PostService] 게시글 무단 조회 시도 - postId: {}, 무단 시도 userId: {}", postId, userId);
                throw new CustomException(PostErrorCode.NOT_POST_OWNER);
            }
        }

        // DTO 변환
        PostResponse response = toPostResponse(post);

        log.info("[PostService] 게시글 조회 - 종료 - postId={}, userId={}", postId, userId);

        return response;
    }

    // 여행 별 게시물 조회 로직
    public List<PostCardResponse> getPostListWithJourney(Long journeyId, Long userId){

        log.info("[PostService] 여행 별 게시글 조회 - 시작 - journeyId={}, userId={}", journeyId, userId);

        Journey journey = journeyRepository.findById(journeyId).orElseThrow(() ->{
            log.warn("[PostService] 여행이 존재하지 않습니다 - journeyId: {}", journeyId);
            return new CustomException(JourneyErrorCode.JOURNEY_NOT_FOUND);
        });

        // 여행 주인이 아니라면? -> 무단 조회
        if(!journey.getUser().getId().equals(userId)){
            log.warn("[PostService] 여행 게시물 리스트 무단 조회 시도 - journeyId: {}, 무단 시도 userId: {}", journeyId, userId);
            throw new CustomException(JourneyErrorCode.NOT_JOURNEY_OWNER);
        }

        // 검증이 끝났다면 정상적으로 조회 시작 -> 생성일시 순서대로 조회
        List<Post> posts = postRepository.findAllByJourneyOrderByCreatedAtAsc(journey);

        // DTO 담을 빈 리스트 선언
        List<PostCardResponse> responseList = new ArrayList<>();

        // 순회하며 DTO 변환
        for(Post post : posts){
            responseList.add(toPostCardResponse(post));
        }

        log.info("[PostService] 여행 별 게시글 조회 - 종료 - 게시글 개수={}", responseList.size());

        return responseList;
    }

    // 게시물 아카이브 조회 로직
    public List<PostCardResponse> getPostsArchive(String scope, Long userId){

        // 기준일(현재는 2주전)
        LocalDate basisDate = LocalDate.now().minusYears(3);

        log.info("[PostService] 게시물 아카이브 조회 - 시작");

        // 요청한 유저 객체 찾기
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("[PostService] 유저 조회 실패 - userId: {}", userId);
            return new CustomException(UserErrorCode.USER_NOT_FOUND);
        });

        List<Post> posts;

        // scope에 따라 글로벌(모든 게시물) 조회 OR 나라 이름(해당 위치한 나라) 게시물 조회
        if (scope.equals("GLOBAL")) {
            posts = postRepository.findByUserNotAndIsPublicAndCreatedDateGreaterThanEqual(user, true, basisDate);
        } else {
            Nation nation = nationRepository.findByEnName(scope).orElseThrow(() -> {
                log.warn("[PostService] 국가를 찾을 수 없습니다. - nation: {}", scope);
                return new CustomException(NationErrorCode.NATION_NOT_FOUND);
            });
            posts = postRepository.findByUserNotAndIsPublicAndNationAndCreatedDateGreaterThanEqual(user, true, nation, basisDate);
        }

        // 랜덤으로 섞기
        Collections.shuffle(posts);

        // 반환 DTO를 담을 빈 리스트 선언
        List<PostCardResponse> responseList = new ArrayList<>();

        // Post 객체 리스트 순회하며 DTO 변환하여 ADD
        for(Post post : posts){
            responseList.add(toPostCardResponse(post));
        }

        // 변환 된 DTO 리스트 반환
        return responseList;
    }

    public PostResponse updatePost(PostUpdateRequest request, Long postId, Long userId){

        List<String> imgUrlList = request.getImgUrlList();

        log.info("[PostService] 게시물 수정 시작 - postId={}, userId={}", postId, userId);

        // 게시물 일단 조회
        Post post = postRepository.findById(postId).orElseThrow(() -> {
            log.warn("[PostService] 게시물을 찾을 수 없습니다 - postId: {}", postId);
            return new CustomException(PostErrorCode.POST_NOT_FOUND);
        });

        // 게시물 무단 수정 시도일 경우 차단
        if(!(post.getUser().getId().equals(userId))){
            log.warn("[PostService] 게시물 무단 수정 시도 - postId : {}, 무단 수정 시도 userId: {}", postId, userId);
            throw new CustomException(PostErrorCode.NOT_POST_OWNER);
        }

        // DB에 원래 저장되어있던 사진
        List<Photo> originalPhotoList = photoRepository.findAllByPostOrderBySeqAsc(post);

        // 기존 DB에 저장되었던 사진들 삭제
        photoRepository.deleteAll(originalPhotoList);

        // 넘겨받은 URL 기준으로 다시 저장
        for(int i = 0; i < imgUrlList.size(); i++){
            // 만약 업데이트를 요청한 사진이 DB에 이미 존재한다면 그대로 유지하고 진행
            Photo photo = Photo.builder()
                    .seq(i)
                    .imgUrl(imgUrlList.get(i))
                    .post(post)
                    .build();

            photoRepository.save(photo);
        }

        // 게시물 최종 업데이트
        post.updatePost(request.getComment(), request.getIsPublic(), imgUrlList.getFirst(), imgUrlList.size());

        // DTO 변환
        PostResponse response = toPostResponse(post);

        return response;
    }

    // 게시물 삭제 로직
    public String deletePost(Long postId, Long userId){

        Post post = postRepository.findById(postId).orElseThrow(() -> {
            log.warn("[PostService] 게시물을 찾을 수 없습니다 - postId: {}", postId);
            return new CustomException(PostErrorCode.POST_NOT_FOUND);
        });

        // 게시물 무단 삭제 시도시 예외처리
        if(!(post.getUser().getId().equals(userId))){
            log.warn("[PostService] 게시물 무단 삭제 시도 - postId : {} , 무단 삭제 시도 userId: {}", postId, userId);
            throw new CustomException(PostErrorCode.NOT_POST_OWNER);
        }

        List<Photo> photoList = photoRepository.findAllByPost(post);

        // 게시물 삭제를 위해 일단, 사진 모두 삭제 하기
        for(Photo photo : photoList){
            photoRepository.delete(photo);
        }

        postRepository.delete(post);

        journeyRepository.decrementPostCount(post.getJourney().getId());



        return "게시물 삭제 성공 - postId : " + postId;
    }

    // 게시물 미리보기 로직
    public PostPreviewResponse getPostPreview(Long postId, Long userId){

        log.info("[PostService] 게시물 미리보기 - 시작 postId={}, userId={}", postId, userId);

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("[PostService] 유저를 찾을 수 없습니다 - userId: {}", userId);
            return new CustomException(UserErrorCode.USER_NOT_FOUND);
        });

        Post post = postRepository.findById(postId).orElseThrow(() -> {
            log.warn("[PostService] 게시물을 찾을 수 없습니다 - postId: {}", postId);
            return new CustomException(PostErrorCode.POST_NOT_FOUND);
        });

        // 만약 게시물이 공개된 상태가 아니라면? -> 무단 조회임
        if(!(post.getIsPublic())){
            log.warn("[PostService] 게시물 무단 조회 시도 - postId: {}, 무단 조회 userId: {}", postId, userId);
            throw new CustomException(PostErrorCode.NOT_POST_OWNER);
        }

        PostPreviewResponse response = toPostPreviewResponse(post);

        return response;
    }

    // 게시물 리스트 형식으로 반환할때 사용하는 카드 DTO 변환 로직
    private PostCardResponse toPostCardResponse(Post post){

        PostCardResponse response = PostCardResponse.builder()
                .postId(post.getId())
                .thumbnailURL(post.getImgUrl())
                .build();

        return response;
    }

    // 일반적 게시물 응답 DTO 변환 로직
    private PostResponse toPostResponse(Post post){

        // DTO에 들어갈 PhotoResponse 리스트 생성
        List<PhotoResponse> photoResponseList = new ArrayList<>();

        // 해당 게시물의 Photo 전부 가져오기
        List<Photo> photoList = photoRepository.findAllByPostOrderBySeqAsc(post);

        // Photo 리스트 순회돌며 이미지 URL add
        for(Photo photo : photoList){
            PhotoResponse temp = PhotoResponse.builder()
                    .photoId(photo.getId())
                    .imgURL(photo.getImgUrl())
                    .build();

            photoResponseList.add(temp);
        }

        // 빌더로 response 생성 후 반환
        PostResponse response = PostResponse.builder()
                .postId(post.getId())
                .nationFlagURL(post.getNation().getImgUrl())
                .nationKRName(post.getNation().getKrName())
                .journeyType(post.getJourney().getType())
                .date(post.getCreatedDate())
                .photoList(photoResponseList)
                .comment(post.getComment())
                .isPublic(post.getIsPublic())
                .photoCount(post.getPhotoCount())
                .commentLength(post.getComment().length())
                .build();

        return response;
    }

    private PostPreviewResponse toPostPreviewResponse(Post post){
        PostPreviewResponse response = PostPreviewResponse.builder()
                .postId(post.getId())
                .nationFlagURL(post.getNation().getImgUrl())
                .nationKRName(post.getNation().getKrName())
                .journeyType(post.getJourney().getType())
                .date(post.getCreatedDate())
                .thumbnailURL(post.getImgUrl())
                .build();

        return response;
    }
}
