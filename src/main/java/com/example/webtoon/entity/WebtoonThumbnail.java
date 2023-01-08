package com.example.webtoon.entity;

import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class WebtoonThumbnail {

    private String fileName;
    private String fileUri;


    public WebtoonThumbnail(String fileName, String fileUri) {
        this.fileName = fileName;
        this.fileUri = fileUri;
    }
}
