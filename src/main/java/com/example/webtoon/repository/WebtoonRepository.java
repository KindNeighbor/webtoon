package com.example.webtoon.repository;

import com.example.webtoon.entity.Webtoon;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebtoonRepository extends JpaRepository<Webtoon, Long> {

    List<Webtoon> findByDayContaining(String day);
    Boolean existsByTitle(String title);
}
