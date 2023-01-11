package com.example.webtoon.service;

import com.example.webtoon.entity.User;
import com.example.webtoon.exception.CustomException;
import com.example.webtoon.type.ErrorCode;
import com.example.webtoon.dto.UserInfo;
import com.example.webtoon.repository.UserRepository;
import com.example.webtoon.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 자기 자신 조회
    public UserInfo getCurrentUser(UserPrincipal currentUser) {
        return new UserInfo(currentUser.getEmail(),
            currentUser.getUsername(),
            currentUser.getNickname());
    }

    // 회원조회(관리자)
    public UserInfo getUserInfo(String nickname) {
        User user = userRepository.findByNickname(nickname)
            .orElseThrow(() -> new CustomException(
                HttpStatus.BAD_REQUEST, ErrorCode.NICKNAME_NOT_FOUND));

        return new UserInfo(user.getEmail(),
                            user.getUsername(),
                            user.getNickname());
    }
}
