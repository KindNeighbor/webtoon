package com.example.webtoon.dto;

import com.example.webtoon.entity.Webtoon;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "webtoon")
@Mapping(mappingPath = "elastic/webtoon-mapping.json")
@Setting(settingPath = "elastic/webtoon-setting.json")
public class WebtoonDocument {

    @Id
    private Long id;

    private String title;

    private String artist;

    private String genre;

    public static WebtoonDocument from(Webtoon webtoon) {

        return WebtoonDocument.builder()
            .id(webtoon.getWebtoonId())
            .title(webtoon.getTitle())
            .artist(webtoon.getArtist())
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
