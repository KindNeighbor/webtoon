package com.example.webtoon.dto;

import com.example.webtoon.entity.Webtoon;
import com.example.webtoon.type.Day;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebtoonDto {

    private Long webtoonId;
    private String title;
    private String artist;
    private Day day;
    private String genre;

    public static WebtoonDto from(Webtoon webtoon) {

        return WebtoonDto.builder()
            .webtoonId(webtoon.getWebtoonId())
            .title(webtoon.getTitle())
            .artist(webtoon.getArtist())
            .day(webtoon.getDay())
            .genre(webtoon.getGenre())
            .build();
    }

    public static WebtoonDto fromDocument(WebtoonDocument webtoonDocument) {

        return WebtoonDto.builder()
            .webtoonId(webtoonDocument.getId())
            .title(webtoonDocument.getTitle())
            .artist(webtoonDocument.getArtist())
            .genre(webtoonDocument.getGenre())
            .build();
    }
}
