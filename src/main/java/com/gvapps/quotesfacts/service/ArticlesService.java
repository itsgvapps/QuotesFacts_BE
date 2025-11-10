package com.gvapps.quotesfacts.service;

import com.gvapps.quotesfacts.entity.ArticlesEntity;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ArticlesService {
    List<ArticlesEntity> getAll();

    Optional<ArticlesEntity> getById(Long id);

    ArticlesEntity save(ArticlesEntity article);

    List<ArticlesEntity> search(String keyword);

    List<ArticlesEntity> findByDate(LocalDate parse);

    List<ArticlesEntity> findTopArticles();

    List<ArticlesEntity> findByTag(String tag);

    Page<ArticlesEntity> getArticlesByTag(String tag, int page, int size);

    void incrementArticleCounts(Map<String, List<Long>> payload);
}
