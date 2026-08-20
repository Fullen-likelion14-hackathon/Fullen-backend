package com.erbe.erbebackend.domain.order.service;

import com.erbe.erbebackend.domain.artist.entity.Artist;
import com.erbe.erbebackend.domain.artist.exception.ArtistErrorCode;
import com.erbe.erbebackend.domain.artist.repository.ArtistRepository;
import com.erbe.erbebackend.domain.bag.entity.UserBag;
import com.erbe.erbebackend.domain.bag.exception.UserBagErrorCode;
import com.erbe.erbebackend.domain.bag.repository.UserBagRepository;
import com.erbe.erbebackend.domain.order.dto.request.InitialApplyRequest;
import com.erbe.erbebackend.domain.order.dto.request.InitialUpdateRequest;
import com.erbe.erbebackend.domain.order.dto.request.OrderRequest;
import com.erbe.erbebackend.domain.order.dto.request.PremiumOrderRequest;
import com.erbe.erbebackend.domain.order.dto.response.*;
import com.erbe.erbebackend.domain.order.entity.Initial;
import com.erbe.erbebackend.domain.order.entity.Order;
import com.erbe.erbebackend.domain.order.entity.PremiumOrder;
import com.erbe.erbebackend.domain.order.enums.OrderStatus;
import com.erbe.erbebackend.domain.order.enums.OrderType;
import com.erbe.erbebackend.domain.order.exception.OrderErrorCode;
import com.erbe.erbebackend.domain.order.repository.InitialRepository;
import com.erbe.erbebackend.domain.order.repository.OrderRepository;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserBagRepository userBagRepository;
    private final PatchPositionRepository patchPositionRepository;
    private final UserRepository userRepository;
    private final PremiumOrderRepository premiumOrderRepository;
    private final PhotoRepository photoRepository;
    private final ArtistRepository artistRepository;
    private final InitialRepository initialRepository;

    // 가방에 달린 이니셜과 패치 주문
    @Transactional
    public OrderResponse createOrder(OrderRequest request, Long userId) {

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
        List<PatchPosition> patchPositions = patchPositionRepository.findAllByUserBagAndIsEditableTrueAndPatchIsNotNull(userBag);

        // 주문이 안된 이니셜이 있는지 조회
        List<Initial> initials = initialRepository.findAllByUserBagAndOrderIsNull(userBag);

        // 주문할 패치가 있는지 조회
        if (patchPositions.isEmpty() && initials.isEmpty()) {
            log.warn("[OrderService] 주문할 패치나 이니셜이 없습니다.");
            throw new CustomException(OrderErrorCode.NOTHING_ORDER);
        }

        // 객체 생성
        Order order = Order.builder()
                .user(user)
                .userBag(userBag)
                .customFrontImgUrl(request.getCustomFrontImgUrl())
                .customBackImgUrl(request.getCustomBackImgUrl())
                .orderStatus(OrderStatus.DELIVERED) // 주문하면 배송 완료 상태로 변경
                .build();

        // DB 저장
        orderRepository.save(order);

        // 주문이 안된 패치를 주문 완료로 변경
        for (PatchPosition position : patchPositions) {
            position.confirmOrder(order);
        }

        // 주문이 안된 이니셜을 주문 완료로 변경
        for (Initial initial : initials) {
            initial.confirmOrder(order);
        }

        // 로그 출력
        log.info("[OrderService] 패치 및 이니셜 주문에 성공했습니다: orderId={}", order.getId());

        // 응답 세팅
        return OrderResponse.builder()
                .orderId(order.getId())
                .userBagId(userBag.getId())
                .orderStatus(order.getOrderStatus())
                .bagName(userBag.getBagProduct().getBag().getName())
                .bagSize(userBag.getBagProduct().getBag().getSize())
                .customFrontImgUrl(order.getCustomFrontImgUrl())
                .customBackImgUrl(order.getCustomBackImgUrl())
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
                .scale(1.0) // 1:1 커스텀은 사용자가 크기 조절을 못하기 때문에 배율을 1.0으로 해서 기본값으로 설정
                .flipped(false) // 1:1 커스텀은 좌우반전을 못하기 때문에 기본값으로 설정
                .layer(0) // 1:1 커스텀은 레이어 순서 개념 없기 때문에 기본값으로 설정
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
                .previewX(request.getPreviewX())
                .previewY(request.getPreviewY())
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
                .previewX(premiumOrder.getPreviewX())
                .previewY(premiumOrder.getPreviewY())
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
                .previewX(premiumOrder.getPreviewX())
                .previewY(premiumOrder.getPreviewY())
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
        Initial initial = Initial.builder()
                .userBag(userBag)
                .initialPhrase(request.getInitialPhrase())
                .color(request.getColor())
                .isBold(request.getIsBold())
                .side(request.getSide())
                .posX(request.getPosX())
                .posY(request.getPosY())
                .rotation(request.getRotation())
                .scale(request.getScale())
                .layer(request.getLayer())
                .build();

        // DB 저장
        initialRepository.save(initial);

        // 로그 출력
        log.info("[OrderService] 이니셜 적용에 성공했습니다: initialOrderId={}", initial.getId());

        // 응답 세팅
        return InitialApplyResponse.builder()
                .initialId(initial.getId())
                .userBagId(userBag.getId())
                .initialPhrase(initial.getInitialPhrase())
                .color(initial.getColor())
                .isBold(initial.isBold())
                .side(initial.getSide())
                .posX(initial.getPosX())
                .posY(initial.getPosY())
                .rotation(initial.getRotation())
                .scale(initial.getScale())
                .layer(initial.getLayer())
                .build();
    }

    // 주문 목록 최신순 조회
    public List<OrderListResponse> orderList(Long userId) {

        // 사용자가 존재하는지 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        // 응답 세팅
        List<OrderListResponse> list = new ArrayList<>();

        // 패치 + 이니셜 주문 목록
        for (Order order : orderRepository.findAllByUserOrderByIdDesc(user)) {
            list.add(OrderListResponse.builder()
                    .type(OrderType.REGULAR)
                    .orderId(order.getId())
                    .frontImgUrl(order.getCustomFrontImgUrl())
                    .backImgUrl(order.getCustomBackImgUrl())
                    .createdAt(order.getCreatedAt())
                    .build());
        }

        // 1:1 커스텀 주문 목록
        for (PremiumOrder premiumOrder : premiumOrderRepository.findAllByUserOrderByIdDesc(user)) {
            list.add(OrderListResponse.builder()
                    .type(OrderType.PREMIUM)
                    .orderId(premiumOrder.getId())
                    .frontImgUrl(premiumOrder.getPatchPosition().getUserBag().getBagProduct().getBag().getFrontImgUrl())
                    .backImgUrl(premiumOrder.getPatchPosition().getUserBag().getBagProduct().getBag().getBackImgUrl())
                    .createdAt(premiumOrder.getCreatedAt())
                    .build());
        }

        // 주문 목록 전체 최신순 정렬
        list.sort(Comparator.comparing(OrderListResponse::getCreatedAt).reversed());

        // 로그 출력
        log.info("[OrderService] 주문 목록 조회 성공");

        return list;
    }

    // 이니셜 적용 조회
    public List<InitialApplyResponse> initialApplyList(Long userId, Long userBagId) {

        // 가방이 존재하는지 조회
        UserBag userBag = userBagRepository.findById(userBagId)
                .orElseThrow(() -> new CustomException(UserBagErrorCode.USER_BAG_NOT_FOUND));

        // 사용자 본인 소유의 가방인지 조회
        if (!userBag.getUser().getId().equals(userId)) {
            log.warn("[OrderService] 본인 소유의 가방이 아닙니다.");
            throw new CustomException(UserBagErrorCode.USER_BAG_ACCESS_DENIED);
        }

        // 응답 세팅
        List<InitialApplyResponse> list = new ArrayList<>();
        for (Initial initial : initialRepository.findAllByUserBag(userBag)) {
            list.add(InitialApplyResponse.builder()
                    .initialId(initial.getId())
                    .userBagId(userBag.getId())
                    .initialPhrase(initial.getInitialPhrase())
                    .color(initial.getColor())
                    .isBold(initial.isBold())
                    .side(initial.getSide())
                    .posX(initial.getPosX())
                    .posY(initial.getPosY())
                    .rotation(initial.getRotation())
                    .scale(initial.getScale())
                    .layer(initial.getLayer())
                    .build());
        }

        // 로그 출력
        log.info("[OrderService] 이니셜 조회 성공");

        return list;
    }

    // 이니셜 수정
    @Transactional
    public InitialApplyResponse updateInitial(InitialUpdateRequest request, Long userId, Long initialId) {

        // 이니셜이 존재하는지 조회
        Initial initial = initialRepository.findById(initialId)
                .orElseThrow(() -> new CustomException(OrderErrorCode.INITIAL_NOT_FOUND));

        // 사용자 본인 소유 가방인지 조회
        if (!initial.getUserBag().getUser().getId().equals(userId)) {
            log.warn("[OrderService] 사용자 본인 가방이 아닙니다.");
            throw new CustomException(UserBagErrorCode.USER_BAG_ACCESS_DENIED);
        }

        // 주문된 이니셜인지 확인
        if (initial.getOrder() != null) {
            log.warn("[OrderService] 주문된 이니셜은 수정할 수 없습니다.");
            throw new CustomException(OrderErrorCode.INITIAL_NOT_EDITABLE);
        }

        // 이니셜 수정
        initial.updateInitial(request.getColor(), request.getIsBold(), request.getSide(), request.getPosX(), request.getPosY(), request.getRotation(), request.getScale(), request.getLayer());

        // 로그 출력
        log.info("[OrderService] 이니셜 수정 성공: initialId={}", initialId);

        // 응답 세팅
        return InitialApplyResponse.builder()
                .initialId(initial.getId())
                .userBagId(initial.getUserBag().getId())
                .initialPhrase(initial.getInitialPhrase())
                .color(initial.getColor())
                .isBold(initial.isBold())
                .side(initial.getSide())
                .posX(initial.getPosX())
                .posY(initial.getPosY())
                .rotation(initial.getRotation())
                .scale(initial.getScale())
                .layer(initial.getLayer())
                .build();
    }

    // 이니셜 삭제
    @Transactional
    public void deleteInitial(Long userId, Long initialId) {

        // 이니셜이 존재하는지 조회
        Initial initial = initialRepository.findById(initialId)
                .orElseThrow(() -> new CustomException(OrderErrorCode.INITIAL_NOT_FOUND));

        // 사용자 본인 소유 가방인지 조회
        if (!initial.getUserBag().getUser().getId().equals(userId)) {
            log.warn("[OrderService] 사용자 본인 가방이 아닙니다.");
            throw new CustomException(UserBagErrorCode.USER_BAG_ACCESS_DENIED);
        }

        // 주문된 이니셜인지 확인
        if (initial.getOrder() != null) {
            log.warn("[OrderService] 주문된 이니셜은 삭제할 수 없습니다.");
            throw new CustomException(OrderErrorCode.INITIAL_NOT_EDITABLE);
        }

        // DB 삭제
        initialRepository.delete(initial);

        // 로그 출력
        log.info("[OrderService] 이니셜 삭제 성공: initialId={}", initialId);
    }
}
