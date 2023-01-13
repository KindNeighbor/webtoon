package com.example.webtoon.service;

import com.example.webtoon.dto.WebtoonIdListDto;
import com.example.webtoon.entity.Favorite;
import com.example.webtoon.entity.User;
import com.example.webtoon.entity.Webtoon;
import com.example.webtoon.exception.CustomException;
import com.example.webtoon.repository.FavRepository;
import com.example.webtoon.repository.UserRepository;
import com.example.webtoon.repository.WebtoonRepository;
import com.example.webtoon.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavService {

    private final WebtoonRepository webtoonRepository;
    private final UserRepository userRepository;
    private final FavRepository favRepository;

    // 선호 작품 등록
    public WebtoonIdListDto addFavWebtoon(Long webtoonId, Long userId) {
        Webtoon webtoon = webtoonRepository.findById(webtoonId).orElseThrow(() -> new CustomException(
            HttpStatus.NOT_FOUND, ErrorCode.WEBTOON_NOT_FOUND));

        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(
            HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));

        if (favRepository.existsByWebtoon_WebtoonIdAndUser_UserId(webtoonId, userId)) {
            throw new CustomException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXIST_FAV_WEBTOON);
        }

        Favorite fav = new Favorite();
        fav.setWebtoon(webtoon);
        fav.setUser(user);
        favRepository.save(fav);
        return new WebtoonIdListDto(webtoonId);
    }

    // 선호 작품 삭제
    public void deleteFavWebtoon(Long webtoonId, Long userId) {
        if (!favRepository.existsByWebtoon_WebtoonIdAndUser_UserId(webtoonId, userId)) {
            throw new CustomException(HttpStatus.NOT_FOUND, ErrorCode.FAV_WEBTOON_NOT_FOUND);
        }
        favRepository.deleteByWebtoon_WebtoonIdAndUser_UserId(webtoonId, userId);
    }
}
