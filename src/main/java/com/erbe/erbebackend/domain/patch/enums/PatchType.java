package com.erbe.erbebackend.domain.patch.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PatchType {
    TICKET,STAMP,LABEL;
}
