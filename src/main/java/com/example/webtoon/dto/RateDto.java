package com.example.webtoon.dto;

import com.example.webtoon.entity.Rate;
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
public class RateDto {

    private Long rateId;
    private Integer userRate;

    public static RateDto from(Rate rate) {

        return RateDto.builder()
            .rateId(rate.getRateId())
            .userRate(rate.getUserRate())
            .build();
    }
}
