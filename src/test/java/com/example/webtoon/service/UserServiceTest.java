package com.example.webtoon.service;

import static com.example.webtoon.type.RoleName.ROLE_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

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
import com.example.webtoon.type.ErrorCode;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Spy
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WebtoonRepository webtoonRepository;
    @Mock
    private EpisodeRepository episodeRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원 조회(관리자) 성공")
    void getUserInfoSuccess() {

        // given
        User user = User.builder()
            .userId(10L)
            .email("test@test.com")
            .username("testUserName")
            .password(passwordEncoder.encode("testPassword"))
            .nickname("testNickName")
            .role(ROLE_USER)
            .build();

        given(userRepository.findByNickname(anyString())).willReturn(Optional.of(user));
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        // when
        UserInfo userInfo = userService.getUserInfo("testNickName");

        // then
        verify(userRepository, times(1)).findByNickname(captor.capture());
        assertEquals(userInfo.getNickname(), captor.getValue());
    }

    @Test
    @DisplayName("회원 조회(관리자) 실패 - 입력한 닉네임과 일치하는 정보 없음")
    void getUserInfoFailed_NicknameNotFound() {

        // given
        given(userRepository.findByNickname(anyString())).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> userService.getUserInfo("testNickName"));

        // then
        assertEquals(BAD_REQUEST, exception.getStatusMessage());
        assertEquals(ErrorCode.NICKNAME_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("유저가 평점 부여한 웹툰 목록 불러오기 성공")
    void getEpisodeRatedByUserSuccess() {

        // given
        Long userId = 1L;
        int page = 0;

        List<Episode> episodeList = Arrays.asList(
            Episode.builder()
                .episodeId(1L)
                .build(),
            Episode.builder()
                .episodeId(2L)
                .build(),
            Episode.builder()
                .episodeId(3L)
                .build()
        );

        Page<Episode> episodes = new PageImpl<>(episodeList);

        given(episodeRepository.findAllByUserId(anyLong(), any())).willReturn(episodes);

        // when
        Page<EpisodeIdListDto> episodeIdListDtoPage =
            userService.getEpisodeRatedByUser(userId, page);

        // then
        assertEquals(1L, episodeIdListDtoPage.getContent().get(0).getEpisodeId());
        assertEquals(2L, episodeIdListDtoPage.getContent().get(1).getEpisodeId());
        assertEquals(3L, episodeIdListDtoPage.getContent().get(2).getEpisodeId());
        assertEquals(1, episodeIdListDtoPage.getTotalPages());
        assertEquals(3, episodeIdListDtoPage.getTotalElements());
    }

    @Test
    @DisplayName("유저가 작성한 댓글 목록 불러오기 성공")
    void getCommentsByUserSuccess() {

        // given
        int page = 0;

        User user = User.builder()
            .userId(1L)
            .build();

        List<Comment> commentList = Arrays.asList(
            Comment.builder()
                .commentId(1L)
                .user(user)
                .build(),
            Comment.builder()
                .commentId(2L)
                .user(user)
                .build(),
            Comment.builder()
                .commentId(3L)
                .user(user)
                .build(),
            Comment.builder()
                .commentId(4L)
                .user(user)
                .build()
        );

        Page<Comment> comments = new PageImpl<>(commentList);

        given(commentRepository.findAllByUser_UserId(anyLong(), any())).willReturn(comments);

        // when
        Page<CommentDto> commentDtoPage =
            userService.getCommentsByUser(user.getUserId(), page);

        // then
        assertEquals(1L, commentDtoPage.getContent().get(0).getCommentId());
        assertEquals(2L, commentDtoPage.getContent().get(1).getCommentId());
        assertEquals(3L, commentDtoPage.getContent().get(2).getCommentId());
        assertEquals(4L, commentDtoPage.getContent().get(3).getCommentId());
        assertEquals(1, commentDtoPage.getTotalPages());
        assertEquals(4, commentDtoPage.getTotalElements());
    }

    @Test
    @DisplayName("선호작품 목록 불러오기 성공")
    void getFavWebtoonListSuccess() {

        // given
        Long userId = 1L;
        int page = 0;

        List<Webtoon> webtoonList = Arrays.asList(
            Webtoon.builder()
                .webtoonId(1L)
                .build(),
            Webtoon.builder()
                .webtoonId(2L)
                .build(),
            Webtoon.builder()
                .webtoonId(3L)
                .build(),
            Webtoon.builder()
                .webtoonId(4L)
                .build()
        );

        Page<Webtoon> webtoons = new PageImpl<>(webtoonList);

        given(webtoonRepository.findAllByUserId(anyLong(), any())).willReturn(webtoons);

        // when
        Page<WebtoonIdListDto> webtoonIdListDtos =
            userService.getFavWebtoonList(userId, page);

        // then
        assertEquals(1L, webtoonIdListDtos.getContent().get(0).getWebtoonId());
        assertEquals(2L, webtoonIdListDtos.getContent().get(1).getWebtoonId());
        assertEquals(3L, webtoonIdListDtos.getContent().get(2).getWebtoonId());
        assertEquals(4L, webtoonIdListDtos.getContent().get(3).getWebtoonId());
        assertEquals(1, webtoonIdListDtos.getTotalPages());
        assertEquals(4, webtoonIdListDtos.getTotalElements());
    }
}