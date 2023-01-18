package com.example.webtoon.repository;

import com.example.webtoon.dto.WebtoonDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface WebtoonSearchRepository extends ElasticsearchRepository<WebtoonDocument, Long> {

    Page<WebtoonDocument> findByTitleOrArtistOrGenre(String title, String artist, String genre, Pageable pageable);
}
