package com.example.webtoon.dto;


import com.example.webtoon.entity.Episode;
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
public class EpisodeDto {

    private Long episodeId;
    private String title;
    private String epFileUri;
    private String thFileUri;

    public static EpisodeDto from(Episode episode) {

        return EpisodeDto.builder()
            .episodeId(episode.getEpisodeId())
            .title(episode.getTitle())
            .epFileUri(episode.getEpisodeFile().getEpFileUri())
            .thFileUri(episode.getEpisodeThumbnail().getThFileUri())
            .build();
    }
}
