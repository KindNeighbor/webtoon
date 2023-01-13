package com.example.webtoon.repository;

import com.example.webtoon.entity.Episode;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface EpisodeRepository extends JpaRepository<Episode, Long> {

    List<Episode> findByWebtoon_WebtoonId(Long webtoonId);
    Boolean existsByWebtoon_WebtoonIdAndTitle(Long webtoonId, String title);

    @Query(value="Select avg(user_rate) from example.rate where episode_id = ?1", nativeQuery = true)
    Double getAvgRate(Long id);

    @Query(value ="SELECT * FROM example.episode "
        + "left join rate r on episode.episode_id = r.episode_id "
        + "WHERE user_id = ?1", nativeQuery = true)
    List<Episode> findAllByUserId(Long userId);
}
