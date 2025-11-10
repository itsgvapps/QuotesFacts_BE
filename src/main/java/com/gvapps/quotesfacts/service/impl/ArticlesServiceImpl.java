package com.gvapps.quotesfacts.service.impl;

import com.gvapps.quotesfacts.entity.ArticlesEntity;
import com.gvapps.quotesfacts.repository.ArticlesRepository;
import com.gvapps.quotesfacts.service.ArticlesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticlesServiceImpl  implements ArticlesService {

    private final ArticlesRepository repository;

    @Override
    public List<ArticlesEntity> findByTag(String tag) {
        return repository.findRandomByTag(tag);
    }

    @Override
    public Page<ArticlesEntity> getArticlesByTag(String tag, int page, int size) {
        return repository.findByTagOrAll(tag, PageRequest.of(page, size));
    }
    @Override
    public List<ArticlesEntity> getAll() {
        return repository.findAll();
    }

    @Override
    public Optional<ArticlesEntity> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public ArticlesEntity save(ArticlesEntity article) {
        return repository.save(article);
    }

    @Override
    public List<ArticlesEntity> search(String keyword) {
        return repository.searchByKeyword(keyword);
    }

    @Override
    public List<ArticlesEntity> findByDate(LocalDate date) {
        return repository.findByAddedDate(date);
    }

    @Override
    public List<ArticlesEntity> findTopArticles() {
        return repository.findByFeaturedTrue();
    }

    @Override
    @Transactional
    public void incrementArticleCounts(Map<String, List<Long>> payload) {

        if (payload.containsKey("views")) {
            List<Long> ids = payload.get("views");
            if (ids != null && !ids.isEmpty()) repository.incrementViews(ids);
        }

        if (payload.containsKey("likes")) {
            List<Long> ids = payload.get("likes");
            if (ids != null && !ids.isEmpty()) repository.incrementLikes(ids);
        }

        if (payload.containsKey("downloads")) {
            List<Long> ids = payload.get("downloads");
            if (ids != null && !ids.isEmpty()) repository.incrementDownloads(ids);
        }

        if (payload.containsKey("favourites")) {
            List<Long> ids = payload.get("favourites");
            if (ids != null && !ids.isEmpty()) repository.incrementFavourites(ids);
        }

        if (payload.containsKey("bookmarks")) {
            List<Long> ids = payload.get("bookmarks");
            if (ids != null && !ids.isEmpty()) repository.incrementBookmarks(ids);
        }
    }
}
