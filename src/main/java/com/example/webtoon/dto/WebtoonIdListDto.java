package com.example.webtoon.dto;

import com.example.webtoon.entity.Webtoon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebtoonIdListDto {

    private Long webtoonId;

    public static WebtoonIdListDto from(Webtoon webtoon) {
        return WebtoonIdListDto.builder()
            .webtoonId(webtoon.getWebtoonId())
            .build();
    }
}
