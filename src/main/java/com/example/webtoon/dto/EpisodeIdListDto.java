package com.example.webtoon.dto;

import com.example.webtoon.entity.Episode;
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
public class EpisodeIdListDto {

    private Long episodeId;

    public static EpisodeIdListDto from(Episode episode) {
        return EpisodeIdListDto.builder()
            .episodeId(episode.getEpisodeId())
            .build();
    }
}
