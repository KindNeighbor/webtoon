package com.example.webtoon;

import com.example.webtoon.config.FileUploadProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableCaching
@EnableConfigurationProperties({FileUploadProperties.class})
public class WebtoonApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebtoonApplication.class, args);
	}

}
