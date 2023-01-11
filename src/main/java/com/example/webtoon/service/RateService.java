package com.example.webtoon.service;

import com.example.webtoon.dto.RateAvgDto;
import com.example.webtoon.dto.RateDto;
import com.example.webtoon.entity.Episode;
import com.example.webtoon.entity.Rate;
import com.example.webtoon.entity.User;
import com.example.webtoon.exception.CustomException;
import com.example.webtoon.repository.EpisodeRepository;
import com.example.webtoon.repository.RateRepository;
import com.example.webtoon.repository.UserRepository;
import com.example.webtoon.repository.WebtoonRepository;
import com.example.webtoon.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@RequiredArgsConstructor
public class RateService {

    private final UserRepository userRepository;
    private final EpisodeRepository episodeRepository;
    private final WebtoonRepository webtoonRepository;
    private final RateRepository rateRepository;

    // 평점 등록
    public RateDto addRate(Long episodeId, Long userId, Integer userRate) {

        if (rateRepository.existsByEpisode_EpisodeIdAndUser_UserId(episodeId, userId)) {
            throw new CustomException(HttpStatus.CONFLICT, ErrorCode.ALREADY_RATED);
        }

        Episode episode = episodeRepository.findById(episodeId).orElseThrow(
            () ->new CustomException(
            HttpStatus.NOT_FOUND, ErrorCode.EPISODE_NOT_FOUND));

        User user = userRepository.findById(userId).orElseThrow(
            () ->new CustomException(
            HttpStatus.NOT_FOUND, ErrorCode.NICKNAME_NOT_FOUND));

        Rate rate = new Rate(userRate);
        rate.setEpisode(episode);
        rate.setUser(user);
        rateRepository.save(rate);

        return RateDto.from(rate);
    }

    // 평점 수정
    public RateDto updateRate(Long episodeId, Long userId, Integer userRate) {
        Rate rate = rateRepository.findByEpisode_EpisodeIdAndUser_UserId(episodeId, userId)
            .orElseThrow(() ->new CustomException(
                HttpStatus.NOT_FOUND, ErrorCode.RATE_NOT_FOUND));

        rate.setUserRate(userRate);
        rateRepository.save(rate);

        return RateDto.from(rate);
    }

    // 웹툰 평점 평균 불러오기
    public RateAvgDto getWebtoonAvgRate(@PathVariable Long webtoonId){
        return new RateAvgDto(webtoonRepository.getAvgRate(webtoonId));
    }

    // 에피소드 평점 평균 불러오기
    public RateAvgDto getWebtoonEpisodeAvgRate(@PathVariable Long episodeId){
        return new RateAvgDto(rateRepository.getAvgRate(episodeId));
    }
}
