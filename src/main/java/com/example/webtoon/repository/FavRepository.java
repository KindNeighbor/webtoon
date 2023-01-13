package com.example.webtoon.repository;

import com.example.webtoon.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface FavRepository extends JpaRepository<Favorite, Long> {

    boolean existsByWebtoon_WebtoonIdAndUser_UserId(Long webtoonId, Long userId);

    void deleteByWebtoon_WebtoonIdAndUser_UserId(Long webtoonId, Long userId);
}
