package com.erbe.erbebackend.domain.patch.repository;

import com.erbe.erbebackend.domain.bag.entity.UserBag;
import com.erbe.erbebackend.domain.order.entity.PatchOrder;
import com.erbe.erbebackend.domain.patch.entity.Patch;
import com.erbe.erbebackend.domain.patch.entity.PatchPosition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatchPositionRepository extends JpaRepository<PatchPosition, Long> {

    // 가방에 적용된 패치 목록 조회
    List<PatchPosition> findAllByUserBag(UserBag userBag);

    // 패치가 가방에 달려있는지 조회
    boolean existsByPatch(Patch patch);

    // 가방에서 주문이 안된 패치 위치 조회
    List<PatchPosition> findAllByUserBagAndIsEditableTrue(UserBag userBag);

    // 패치 주문에 포함된 패치 위치 조회
    List<PatchPosition> findAllByPatchOrder(PatchOrder patchOrder);
}
