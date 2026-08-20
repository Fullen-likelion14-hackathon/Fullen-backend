package com.erbe.erbebackend.domain.user.service;

import com.erbe.erbebackend.domain.user.entity.User;
import com.erbe.erbebackend.domain.user.exception.UserErrorCode;
import com.erbe.erbebackend.domain.user.repository.UserRepository;
import com.erbe.erbebackend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public String getUserName(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("[UserService] 유저를 찾을 수 없습니다.");
            return new CustomException(UserErrorCode.USER_NOT_FOUND);
        });

        String username = user.getNickname();

        return username;
    }
}
