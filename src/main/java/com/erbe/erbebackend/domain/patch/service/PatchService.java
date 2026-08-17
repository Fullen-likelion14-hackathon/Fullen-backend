package com.erbe.erbebackend.domain.patch.service;

import com.erbe.erbebackend.domain.bag.entity.UserBag;
import com.erbe.erbebackend.domain.bag.exception.UserBagErrorCode;
import com.erbe.erbebackend.domain.bag.repository.UserBagRepository;
import com.erbe.erbebackend.domain.patch.dto.request.PatchApplyRequest;
import com.erbe.erbebackend.domain.patch.dto.request.PatchPositionUpdateRequest;
import com.erbe.erbebackend.domain.patch.dto.request.PatchSaveRequest;
import com.erbe.erbebackend.domain.patch.dto.response.PatchApplyResponse;
import com.erbe.erbebackend.domain.patch.dto.response.PatchListResponse;
import com.erbe.erbebackend.domain.patch.dto.response.PatchSaveResponse;
import com.erbe.erbebackend.domain.patch.entity.Patch;
import com.erbe.erbebackend.domain.patch.entity.PatchPosition;
import com.erbe.erbebackend.domain.patch.enums.PatchType;
import com.erbe.erbebackend.domain.patch.exception.PatchErrorCode;
import com.erbe.erbebackend.domain.patch.repository.PatchPositionRepository;
import com.erbe.erbebackend.domain.patch.repository.PatchRepository;
import com.erbe.erbebackend.domain.user.entity.User;
import com.erbe.erbebackend.domain.user.exception.UserErrorCode;
import com.erbe.erbebackend.domain.user.repository.UserRepository;
import com.erbe.erbebackend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PatchService {

    private final PatchRepository patchRepository;
    private final UserRepository userRepository;
    private final UserBagRepository userBagRepository;
    private final PatchPositionRepository patchPositionRepository;

    // 패치 생성
    @Transactional
    public PatchSaveResponse savePatch(PatchSaveRequest request, Long userId) {

        // 사용자가 존재하는지 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        // 패치가 저장되었는지 조회
        if (patchRepository.existsByUserAndImgUrl(user, request.getImgUrl())) {
            log.warn("[PatchService] 이미 저장된 패치입니다.");
            throw new CustomException(PatchErrorCode.PATCH_ALREADY_SAVED);
        }

        // Patch 객체 생성
        Patch patch = Patch.builder()
                .user(user)
                .type(request.getType())
                .imgUrl(request.getImgUrl())
                .build();

        // DB 저장
        patchRepository.save(patch);

        // 로그 출력
        log.info("[PatchService] 패치 저장 성공: patchId={}", patch.getId());

        // 응답 세팅
        return PatchSaveResponse.builder()
                .patchId(patch.getId())
                .type(patch.getType())
                .imgUrl(patch.getImgUrl())
                .build();
    }

    // 패치 리스트 조회
    public List<PatchListResponse> patchList(Long userId, PatchType type) {

        // 사용자가 존재하는지 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        // 응답 세팅
        List<PatchListResponse> list = new ArrayList<>();
        for (Patch patch : patchRepository.findAllByUserAndTypeOrderByIdDesc(user, type)) {
            list.add(PatchListResponse.builder()
                    .patchId(patch.getId())
                    .type(patch.getType())
                    .imgUrl(patch.getImgUrl())
                    .build());
        }

        // 로그 출력
        log.info("[PatchService] 패치 리스트 조회 성공");

        return list;
    }

    // 패치 삭제
    @Transactional
    public void patchDelete(Long patchId, Long userId) {

        // 삭제할 패치가 존재하는지 조회
        Patch patch = patchRepository.findById(patchId)
                .orElseThrow(() -> new CustomException(PatchErrorCode.PATCH_NOT_FOUND));

        // 로그인한 사용자의 패치가 맞는지 조회
        if (!patch.getUser().getId().equals(userId)) {
            log.warn("[PatchService] 사용자 본인 소유의 패치가 아닙니다.");
            throw new CustomException(PatchErrorCode.PATCH_ACCESS_DENIED);
        }

        // 패치가 사용중인지 확인
        if (patchPositionRepository.existsByPatch(patch)) {
            log.warn("[PatchService] 가방에 적용된 패치는 삭제할 수 없습니다.");
            throw new CustomException(PatchErrorCode.PATCH_IN_USE);
        }

        // DB 삭제
        patchRepository.delete(patch);

        // 로그 출력
        log.info("[PatchService] 패치 삭제 성공");
    }

    // 패치를 가방에 적용
    @Transactional
    public PatchApplyResponse applyPatch(PatchApplyRequest request, Long userId) {

        // 가방이 존재하는지 조회
        UserBag userBag = userBagRepository.findById(request.getUserBagId())
                .orElseThrow(() -> new CustomException(UserBagErrorCode.USER_BAG_NOT_FOUND));

        // 패치가 존재하는지 조회
        Patch patch = patchRepository.findById(request.getPatchId())
                .orElseThrow(() -> new CustomException(PatchErrorCode.PATCH_NOT_FOUND));

        // 사용자 본인 소유의 가방인지 확인
        if (!userBag.getUser().getId().equals(userId)) {
            log.warn("[PatchService] 사용자 본인 소유의 가방이 아닙니다.");
            throw new CustomException(UserBagErrorCode.USER_BAG_ACCESS_DENIED);
        }

        // 사용자 본인 소유의 패치인지 확인
        if (!patch.getUser().getId().equals(userId)) {
            log.warn("[PatchService] 사용자 본인 소유의 패치가 아닙니다.");
            throw new CustomException(PatchErrorCode.PATCH_ACCESS_DENIED);
        }

        // 객체 생성
        PatchPosition patchPosition = PatchPosition.builder()
                .userBag(userBag)
                .patch(patch)
                .side(request.getSide())
                .posX(request.getPosX())
                .posY(request.getPosY())
                .rotation(request.getRotation())
                .build();

        // DB 저장
        patchPositionRepository.save(patchPosition);

        // 로그 출력
        log.info("[PatchService] 가방에 패치 적용 완료: patchPositionId={}", patchPosition.getId());

        // 응답 세팅
        return PatchApplyResponse.builder()
                .patchPositionId(patchPosition.getId())
                .userBagId(patchPosition.getUserBag().getId())
                .patchId(patchPosition.getPatch().getId())
                .imgUrl(patchPosition.getPatch().getImgUrl())
                .side(patchPosition.getSide())
                .posX(patchPosition.getPosX())
                .posY(patchPosition.getPosY())
                .rotation(patchPosition.getRotation())
                .isEditable(patchPosition.getIsEditable())
                .build();
    }

    // 가방에 부착된 패치 리스트 조회
    public List<PatchApplyResponse> patchPositionList(Long userId, Long userBagId) {

        // 가방이 존재하는지 조회
        UserBag userBag = userBagRepository.findById(userBagId)
                .orElseThrow(() -> new CustomException(UserBagErrorCode.USER_BAG_NOT_FOUND));

        // 사용자 본인 소유의 가방인지 확인
        if (!userBag.getUser().getId().equals(userId)) {
            log.warn("[PatchService] 사용자 본인 소유의 가방이 아닙니다.");
            throw new CustomException(UserBagErrorCode.USER_BAG_ACCESS_DENIED);
        }

        // 응답 세팅
        List<PatchApplyResponse> list = new ArrayList<>();
        for (PatchPosition patchPosition : patchPositionRepository.findAllByUserBag(userBag)) {
            list.add(PatchApplyResponse.builder()
                    .patchPositionId(patchPosition.getId())
                    .userBagId(patchPosition.getUserBag().getId())
                    .patchId(patchPosition.getPatch().getId())
                    .imgUrl(patchPosition.getPatch().getImgUrl())
                    .side(patchPosition.getSide())
                    .posX(patchPosition.getPosX())
                    .posY(patchPosition.getPosY())
                    .rotation(patchPosition.getRotation())
                    .isEditable(patchPosition.getIsEditable())
                    .build());
        }

        // 로그 출력
        log.info("[PatchService] 가방과 가방에 부착된 패치 조회 성공");

        return list;
    }

    // 패치 위치 수정
    @Transactional
    public PatchApplyResponse updatePatchPosition(Long userId, Long patchPositionId, PatchPositionUpdateRequest request) {

        // 패치 위치가 존재하는지 확인
        PatchPosition patchPosition = patchPositionRepository.findById(patchPositionId)
                .orElseThrow(() -> new CustomException(PatchErrorCode.PATCH_POSITION_NOT_FOUND));

        // 사용자 소유의 가방인지 조회
        if (!patchPosition.getUserBag().getUser().getId().equals(userId)) {
            log.warn("[PatchService] 사용자 본인 소유의 가방이 아닙니다.");
            throw new CustomException(UserBagErrorCode.USER_BAG_ACCESS_DENIED);
        }

        // 주문완료가 된 상태인지 조회
        if (!patchPosition.getIsEditable()) {
            log.warn("[PatchService] 주문 완료가 된 패치는 수정할 수 없습니다.");
            throw new CustomException(PatchErrorCode.PATCH_POSITION_NOT_EDITABLE);
        }

        // 패치 위치 수정
        patchPosition.updatePosition(request.getSide(), request.getPosX(), request.getPosY(), request.getRotation());

        // 로그 출력
        log.info("[PatchService] 가방에 부착된 패치 위치 수정 완료: patchPositionId={}", patchPosition.getId());

        // 응답 세팅
        return PatchApplyResponse.builder()
                .patchPositionId(patchPosition.getId())
                .userBagId(patchPosition.getUserBag().getId())
                .patchId(patchPosition.getPatch().getId())
                .imgUrl(patchPosition.getPatch().getImgUrl())
                .side(patchPosition.getSide())
                .posX(patchPosition.getPosX())
                .posY(patchPosition.getPosY())
                .rotation(patchPosition.getRotation())
                .isEditable(patchPosition.getIsEditable())
                .build();
    }

    // 패치 위치 삭제
    @Transactional
    public void patchPositionDelete(Long userId, Long patchPositionId) {

        // 패치 위치가 존재하는지 확인
        PatchPosition patchPosition = patchPositionRepository.findById(patchPositionId)
                .orElseThrow(() -> new CustomException(PatchErrorCode.PATCH_POSITION_NOT_FOUND));

        // 사용자 소유의 가방인지 조회
        if (!patchPosition.getUserBag().getUser().getId().equals(userId)) {
            log.warn("[PatchService] 사용자 소유의 가방이 아닙니다.");
            throw new CustomException(UserBagErrorCode.USER_BAG_ACCESS_DENIED);
        }

        // 주문완료가 된 상태인지 조회
        if (!patchPosition.getIsEditable()) {
            log.warn("[PatchService] 주문 완료가 된 패치는 삭제할 수 없습니다.");
            throw new CustomException(PatchErrorCode.PATCH_POSITION_NOT_EDITABLE);
        }

        // DB 삭제
        patchPositionRepository.delete(patchPosition);

        // 로그 출력
        log.info("[PatchService] 패치 위치 삭제 성공");
    }
}
