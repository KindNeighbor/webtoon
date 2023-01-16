package com.example.webtoon.service;

import com.example.webtoon.dto.CommentDto;
import com.example.webtoon.dto.EpisodeIdListDto;
import com.example.webtoon.dto.UserInfo;
import com.example.webtoon.dto.WebtoonIdListDto;
import com.example.webtoon.entity.Comment;
import com.example.webtoon.entity.Episode;
import com.example.webtoon.entity.User;
import com.example.webtoon.entity.Webtoon;
import com.example.webtoon.exception.CustomException;
import com.example.webtoon.repository.CommentRepository;
import com.example.webtoon.repository.EpisodeRepository;
import com.example.webtoon.repository.UserRepository;
import com.example.webtoon.repository.WebtoonRepository;
import com.example.webtoon.security.UserPrincipal;
import com.example.webtoon.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final int SIZE = 10;

    private final UserRepository userRepository;
    private final WebtoonRepository webtoonRepository;
    private final EpisodeRepository episodeRepository;
    private final CommentRepository commentRepository;

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

    // 유저가 평점 부여한 웹툰 목록 불러오기
    public Page<EpisodeIdListDto> getEpisodeRatedByUser(Long userId, Integer page) {
        Pageable pageable = PageRequest.of(page, SIZE);
        Page<Episode> episodeList = episodeRepository.findAllByUserId(userId, pageable);
        return episodeList.map(EpisodeIdListDto::from);
    }

    // 유저가 작성한 댓글 목록 조회
    public Page<CommentDto> getCommentsByUser(Long userId, Integer page) {
        Pageable pageable = PageRequest.of(page, SIZE);
        Page<Comment> commentList = commentRepository.findAllByUser_UserId(userId, pageable);
        return commentList.map(CommentDto::from);
    }

    // 선호 작품 목록 조회
    public Page<WebtoonIdListDto> getFavWebtoonList(Long userId, Integer page) {
        Pageable pageable = PageRequest.of(page, SIZE);
        Page<Webtoon> webtoonList = webtoonRepository.findAllByUserId(userId, pageable);
        return webtoonList.map(WebtoonIdListDto::from);
    }
}
