package com.example.webtoon.repository;

import com.example.webtoon.entity.Webtoon;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface WebtoonRepository extends JpaRepository<Webtoon, Long> {

    List<Webtoon> findByDayContaining(String day);
    Boolean existsByTitle(String title);

    @Query(value="Select avg(user_rate) from example.rate "
        + "JOIN example.episode e on e.episode_id = rate.episode_id "
        + "where webtoon_id = ?1", nativeQuery = true)
    Double getAvgRate(Long id);

    @Query(value = "SELECT * FROM example.webtoon "
        + "LEFT JOIN example.favorite f on webtoon.webtoon_id = f.webtoon_id "
        + "WHERE user_id = ?1", nativeQuery = true)
    Page<Webtoon> findAllByUserId(Long userId, Pageable pageable);

    @Query(value = "SELECT * FROM example.webtoon w "
        + "LEFT JOIN (SELECT e.webtoon_id, avg(user_rate) as avg_rate FROM example.rate "
        + "LEFT JOIN example.episode e ON e.episode_id = rate.episode_id group by webtoon_id) user_rate "
        + "ON w.webtoon_id = user_rate.webtoon_id WHERE w.day LIKE CONCAT('%',:day,'%')",
        countQuery = "SELECT COUNT(*) FROM example.webtoon WHERE day LIKE CONCAT('%',:day,'%')",
        nativeQuery = true)
    Page<Webtoon> findByDay(@Param("day") String day, Pageable pageable);

    boolean existsByDayContaining(String day);
}
