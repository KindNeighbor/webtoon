package com.example.webtoon.entity;

import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class EpisodeThumbnail {

    private String thFileName;
    private String thFileUri;

    public EpisodeThumbnail(String thFileName, String thFileUri) {
        this.thFileName = thFileName;
        this.thFileUri = thFileUri;
    }
}
