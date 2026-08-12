package com.erbe.erbebackend.domain.order.service;

import com.erbe.erbebackend.domain.artist.entity.Artist;
import com.erbe.erbebackend.domain.artist.exception.ArtistErrorCode;
import com.erbe.erbebackend.domain.artist.repository.ArtistRepository;
import com.erbe.erbebackend.domain.bag.entity.UserBag;
import com.erbe.erbebackend.domain.bag.exception.UserBagErrorCode;
import com.erbe.erbebackend.domain.bag.repository.UserBagRepository;
import com.erbe.erbebackend.domain.order.dto.request.InitialApplyRequest;
import com.erbe.erbebackend.domain.order.dto.request.PatchOrderRequest;
import com.erbe.erbebackend.domain.order.dto.request.PremiumOrderRequest;
import com.erbe.erbebackend.domain.order.dto.response.InitialApplyResponse;
import com.erbe.erbebackend.domain.order.dto.response.PatchOrderResponse;
import com.erbe.erbebackend.domain.order.dto.response.PremiumOrderResponse;
import com.erbe.erbebackend.domain.order.dto.response.PremiumOrderSearchResponse;
import com.erbe.erbebackend.domain.order.entity.InitialOrder;
import com.erbe.erbebackend.domain.order.entity.PatchOrder;
import com.erbe.erbebackend.domain.order.entity.PremiumOrder;
import com.erbe.erbebackend.domain.order.enums.OrderStatus;
import com.erbe.erbebackend.domain.order.exception.OrderErrorCode;
import com.erbe.erbebackend.domain.order.repository.InitialOrderRepository;
import com.erbe.erbebackend.domain.order.repository.PatchOrderRepository;
import com.erbe.erbebackend.domain.order.repository.PremiumOrderRepository;
import com.erbe.erbebackend.domain.patch.entity.PatchPosition;
import com.erbe.erbebackend.domain.patch.repository.PatchPositionRepository;
import com.erbe.erbebackend.domain.photo.entity.Photo;
import com.erbe.erbebackend.domain.photo.exception.PhotoErrorCode;
import com.erbe.erbebackend.domain.photo.repository.PhotoRepository;
import com.erbe.erbebackend.domain.user.entity.User;
import com.erbe.erbebackend.domain.user.exception.UserErrorCode;
import com.erbe.erbebackend.domain.user.repository.UserRepository;
import com.erbe.erbebackend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderService {

    private final PatchOrderRepository patchOrderRepository;
    private final UserBagRepository userBagRepository;
    private final PatchPositionRepository patchPositionRepository;
    private final UserRepository userRepository;
    private final PremiumOrderRepository premiumOrderRepository;
    private final PhotoRepository photoRepository;
    private final ArtistRepository artistRepository;
    private final InitialOrderRepository initialOrderRepository;

    // 가방에 패치 부착하고 주문
    @Transactional
    public PatchOrderResponse patchOrder(PatchOrderRequest request, Long userId) {

        // 사용자가 존재하는지 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        // 가방이 존재하는지 조회
        UserBag userBag = userBagRepository.findById(request.getUserBagId())
                .orElseThrow(() -> new CustomException(UserBagErrorCode.USER_BAG_NOT_FOUND));

        // 사용자 본인 소유의 가방인지 확인
        if (!userBag.getUser().getId().equals(userId)) {
            log.warn("[OrderService] 본인 소유의 가방이 아닙니다.");
            throw new CustomException(UserBagErrorCode.USER_BAG_ACCESS_DENIED);
        }

        // 주문이 안된 패치가 있는지 조회
        List<PatchPosition> patchPosition = patchPositionRepository.findAllByUserBagAndIsEditableTrueAndPatchIsNotNull(userBag);

        // 주문할 패치가 있는지 조회
        if (patchPosition.isEmpty()) {
            log.warn("[OrderService] 가방에 부착된 패치가 없어 주문할 수 없습니다.");
            throw new CustomException(OrderErrorCode.NO_PATCH_ATTACH);
        }

        // 객체 생성
        PatchOrder patchOrder = PatchOrder.builder()
                .user(user)
                .userBag(userBag)
                .orderStatus(OrderStatus.DELIVERED) // 주문하면 배송 완료 상태로 변경
                .build();

        // DB 저장
        patchOrderRepository.save(patchOrder);

        // 주문이 안된 패치를 주문 완료로 변경
        for (PatchPosition position : patchPosition) {
            position.confirmOrder(patchOrder);
        }

        // 로그 출력
        log.info("[OrderService] 패치 주문에 성공했습니다: patchOrderId={}", patchOrder.getId());

        // 응답 세팅
        return PatchOrderResponse.builder()
                .patchOrderId(patchOrder.getId())
                .userBagId(userBag.getId())
                .orderStatus(patchOrder.getOrderStatus())
                .bagName(userBag.getBagProduct().getBag().getName())
                .bagSize(userBag.getBagProduct().getBag().getSize())
                .bagFrontImgUrl(userBag.getBagProduct().getBag().getFrontImgUrl())
                .bagBackImgUrl(userBag.getBagProduct().getBag().getBackImgUrl())
                .build();
    }

    // 1:1 커스텀 요청
    @Transactional
    public PremiumOrderResponse premiumOrder(PremiumOrderRequest request, Long userId) {

        // 사용자가 존재하는지 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        // 가방이 존재하는지 조회
        UserBag userBag = userBagRepository.findById(request.getUserBagId())
                .orElseThrow(() -> new CustomException(UserBagErrorCode.USER_BAG_NOT_FOUND));

        // 사용자 본인 소유의 가방인지 조회
        if (!userBag.getUser().getId().equals(userId)) {
            log.warn("[OrderService] 본인 소유의 가방이 아닙니다.");
            throw new CustomException(UserBagErrorCode.USER_BAG_ACCESS_DENIED);
        }

        // 사진이 존재하는지 조회
        Photo photo = photoRepository.findById(request.getPhotoId())
                .orElseThrow(() -> new CustomException(PhotoErrorCode.PHOTO_NOT_FOUND));

        // 사용자 피드에 있는 사진인지 조회
        if (!photo.getPost().getUser().getId().equals(userId)) {
            log.warn("[OrderService] 본인이 올린 사진이 아닙니다.");
            throw new CustomException(PhotoErrorCode.PHOTO_ACCESS_DENIED);
        }

        // 작가가 존재하는지 조회
        Artist artist = artistRepository.findById(request.getArtistId())
                .orElseThrow(() -> new CustomException(ArtistErrorCode.ARTIST_NOT_FOUND));

        // 패치 부착 위치 설정
        PatchPosition patchPosition = PatchPosition.builder()
                .userBag(userBag)
                .side(request.getSide())
                .posX(request.getPosX())
                .posY(request.getPosY())
                .rotation(request.getRotation())
                .build();

        // 패치 위치 DB 저장
        patchPositionRepository.save(patchPosition);

        // 객체 생성
        PremiumOrder premiumOrder = PremiumOrder.builder()
                .user(user)
                .artist(artist)
                .photo(photo)
                .patchPosition(patchPosition)
                .requestDetail(request.getRequestDetail())
                .orderStatus(OrderStatus.ORDER_COMPLETED)
                .build();

        // DB 저장
        premiumOrderRepository.save(premiumOrder);

        // 로그 출력
        log.info("[OrderService] 1:1 커스텀 요청 성공: premiumOrderId={}", premiumOrder.getId());

        // 응답 세팅
        return PremiumOrderResponse.builder()
                .premiumOrderId(premiumOrder.getId())
                .userBagId(userBag.getId())
                .photoId(photo.getId())
                .artistId(artist.getId())
                .requestDetail(premiumOrder.getRequestDetail())
                .side(patchPosition.getSide())
                .posX(patchPosition.getPosX())
                .posY(patchPosition.getPosY())
                .rotation(patchPosition.getRotation())
                .orderStatus(premiumOrder.getOrderStatus())
                .build();
    }

    // 1:1 커스텀 요청 상세조회
    public PremiumOrderSearchResponse premiumOrderSearch(Long premiumOrderId, Long userId) {

        // 1:1 커스텀 요청 주문이 존재하는지 조회
        PremiumOrder premiumOrder = premiumOrderRepository.findById(premiumOrderId)
                .orElseThrow(() -> new CustomException(OrderErrorCode.PREMIUM_ORDER_NOT_FOUND));

        // 본인이 신청한 1:1 요청인지 확인
        if (!premiumOrder.getUser().getId().equals(userId)) {
            log.warn("[OrderService] 본인이 신청한 1:1 커스텀 요청 주문이 아닙니다.");
            throw new CustomException(OrderErrorCode.PREMIUM_ORDER_ACCESS_DENIED);
        }

        // 로그 출력
        log.info("[OrderService] 1:1 커스텀 요청 주문 상세조회 성공: premiumOrderId={}", premiumOrder.getId());

        // 응답 세팅
        return PremiumOrderSearchResponse.builder()
                .premiumOrderId(premiumOrder.getId())
                .photoImgUrl(premiumOrder.getPhoto().getImgUrl())
                .artistName(premiumOrder.getArtist().getName())
                .artistImgUrl(premiumOrder.getArtist().getImgUrl())
                .introSummary(premiumOrder.getArtist().getIntroSummary())
                .requestDetail(premiumOrder.getRequestDetail())
                .build();
    }

    // 이니셜 적용
    @Transactional
    public InitialApplyResponse applyInitial(InitialApplyRequest request, Long userId) {

        // 가방이 존재하는지 조회
        UserBag userBag = userBagRepository.findById(request.getUserBagId())
                .orElseThrow(() -> new CustomException(UserBagErrorCode.USER_BAG_NOT_FOUND));

        // 사용자 본인 소유의 가방인지 조회
        if (!userBag.getUser().getId().equals(userId)) {
            log.warn("[OrderService] 본인 소유 가방이 아닙니다.");
            throw new CustomException(UserBagErrorCode.USER_BAG_ACCESS_DENIED);
        }

        // 객체 생성
        InitialOrder initialOrder = InitialOrder.builder()
                .userBag(userBag)
                .initialPhrase(request.getInitialPhrase())
                .color(request.getColor())
                .isBold(request.getIsBold())
                .build();

        // DB 저장
        initialOrderRepository.save(initialOrder);

        // 로그 출력
        log.info("[OrderService] 이니셜 적용에 성공했습니다: initialOrderId={}", initialOrder.getId());

        // 응답 세팅
        return InitialApplyResponse.builder()
                .initialOrderId(initialOrder.getId())
                .userBagId(userBag.getId())
                .initialPhrase(initialOrder.getInitialPhrase())
                .color(initialOrder.getColor())
                .isBold(initialOrder.isBold())
                .build();
    }
}
