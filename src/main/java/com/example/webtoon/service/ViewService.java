package com.example.webtoon.service;

import com.example.webtoon.config.RequestUtils;
import com.example.webtoon.entity.View;
import com.example.webtoon.entity.Webtoon;
import com.example.webtoon.exception.CustomException;
import com.example.webtoon.repository.ViewRepository;
import com.example.webtoon.repository.WebtoonRepository;
import com.example.webtoon.type.ErrorCode;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ViewService {

    private final WebtoonRepository webtoonRepository;
    private final ViewRepository viewRepository;

    @Transactional
    public void checkViewCount(Long webtoonId, HttpServletRequest request) {
        Webtoon webtoon = webtoonRepository.findById(webtoonId).orElseThrow(
            () -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.WEBTOON_NOT_FOUND));

        String ip = RequestUtils.getClientIp(request);

        if (!viewRepository.existsByUserIPAndWebtoon_WebtoonId(ip, webtoonId)) {
            View view = new View();
            view.setUserIP(ip);
            view.setWebtoon(webtoon);
            viewRepository.save(view);
            webtoon.setViewCount(webtoon.getViewCount() + 1);
            webtoonRepository.save(webtoon);
        }
    }
}
