package com.example.webtoon.repository;

import com.example.webtoon.entity.Webtoon;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WebtoonRepository extends JpaRepository<Webtoon, Long> {

    List<Webtoon> findByDayContaining(String day);
    Boolean existsByTitle(String title);

    @Query(value="Select avg(user_rate) from example.rate "
        + "JOIN example.episode e on e.episode_id = rate.episode_id "
        + "where webtoon_id = ?1", nativeQuery = true)
    Double getAvgRate(Long id);
}
