package com.example.webtoon.repository;

import com.example.webtoon.entity.View;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViewRepository extends JpaRepository<View, Long> {

    boolean existsByUserIPAndWebtoon_WebtoonId(String ip, Long webtoonId);
}
