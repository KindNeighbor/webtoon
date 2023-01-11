package com.example.webtoon.entity;

import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class EpisodeFile {

    private String epFileName;
    private String epFileUri;

    public EpisodeFile(String epFileName, String epFileUri) {
        this.epFileName = epFileName;
        this.epFileUri = epFileUri;
    }
}
