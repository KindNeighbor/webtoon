package com.example.webtoon.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
public class Episode extends DateEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long episodeId;

    private String title;

    @Embedded
    private EpisodeFile episodeFile;

    @Embedded
    private EpisodeThumbnail episodeThumbnail;

    @ManyToOne
    @JoinColumn(name = "webtoon_id")
    private Webtoon webtoon;

    public Episode(String title) {
        this.title = title;
    }
}
