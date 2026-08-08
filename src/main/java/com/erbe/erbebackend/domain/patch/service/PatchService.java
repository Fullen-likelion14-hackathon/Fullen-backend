package com.erbe.erbebackend.domain.patch.service;

import com.erbe.erbebackend.domain.patch.dto.request.PatchSaveRequest;
import com.erbe.erbebackend.domain.patch.dto.response.PatchListResponse;
import com.erbe.erbebackend.domain.patch.dto.response.PatchSaveResponse;
import com.erbe.erbebackend.domain.patch.entity.Patch;
import com.erbe.erbebackend.domain.patch.enums.PatchType;
import com.erbe.erbebackend.domain.patch.exception.PatchErrorCode;
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

        // DB 삭제
        patchRepository.delete(patch);

        // 로그 출력
        log.info("[PatchService] 패치 삭제 성공");
    }
}
