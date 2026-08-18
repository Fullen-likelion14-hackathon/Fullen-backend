package com.erbe.erbebackend.domain.nation.service;

import com.erbe.erbebackend.domain.nation.dto.response.NationResponse;
import com.erbe.erbebackend.domain.nation.entity.Nation;
import com.erbe.erbebackend.domain.nation.repository.NationRepository;
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
public class NationService {

    private final NationRepository nationRepository;

    public List<NationResponse> getNations() {
        List<Nation> nations = nationRepository.findAll();

        List<NationResponse> responseList = new ArrayList<>();

        for(Nation nation : nations) {
            responseList.add(toNationResponse(nation));
        }

        return responseList;
    }

    private NationResponse toNationResponse(Nation nation) {
        return NationResponse.builder()
                .nationKRName(nation.getKrName())
                .nationENName(nation.getEnName())
                .build();
    }
}
