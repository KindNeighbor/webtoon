package com.example.webtoon.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Rate extends DateEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rateId;

    private Integer userRate;

    @ManyToOne
    @JoinColumn(name = "episode_id")
    private Episode episode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Rate(Integer userRate) {
        this.userRate = userRate;
    }
}
