package com.example.webtoon.entity;

import com.example.webtoon.type.Day;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Webtoon extends DateEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long webtoonId;

    @Column(unique = true)
    private String title;

    private String artist;

    @Enumerated(EnumType.STRING)
    private Day day;

    private String genre;

    private Long viewCount;

    @Embedded
    private WebtoonThumbnail webtoonThumbnail;

    @OneToMany(fetch = FetchType.LAZY,
               cascade = CascadeType.ALL,
               orphanRemoval = true,
               mappedBy = "webtoon")
    private List<Episode> episodes = new ArrayList<>();

    public Webtoon(String title, String artist, Day day, String genre) {
        this.title = title;
        this.artist = artist;
        this.day = day;
        this.genre = genre;
    }
}
