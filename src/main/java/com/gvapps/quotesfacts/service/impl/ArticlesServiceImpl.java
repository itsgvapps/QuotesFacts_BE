package com.gvapps.quotesfacts.service.impl;

import com.gvapps.quotesfacts.dto.ArticleDetailDTO;
import com.gvapps.quotesfacts.dto.ArticleListDTO;
import com.gvapps.quotesfacts.dto.ArticleListProjection;
import com.gvapps.quotesfacts.entity.ArticlesEntity;
import com.gvapps.quotesfacts.exception.ApiException;
import com.gvapps.quotesfacts.mapper.ArticleMapper;
import com.gvapps.quotesfacts.repository.ArticlesRepository;
import com.gvapps.quotesfacts.service.ArticlesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticlesServiceImpl implements ArticlesService {

    private final ArticlesRepository repository;

    @Override
    public List<ArticlesEntity> findByTag(String tag) {
        try {
            return repository.findRandomByTag(tag, 4);
        } catch (Exception e) {
            log.error("[ArticlesServiceImpl] >> findByTag failed for tag: {}", tag, e);
            throw new ApiException("500", "Failed to fetch articles by tag");
        }
    }

    @Override
    public Page<ArticleListDTO> getArticlesByTag(String tag, int page, int size) {
        try {
            tag = tag.toLowerCase().trim();

            Page<ArticleListProjection> projections =
                    repository.findByTagOrAll(tag, PageRequest.of(page, size));

            return projections.map(ArticleMapper::toDTO);

        } catch (Exception e) {
            log.error("[ArticlesServiceImpl] >> getArticlesByTag failed for tag: {}", tag, e);
            throw new ApiException("500", "Failed to fetch paginated articles by tag");
        }
    }


    @Override
    public List<ArticlesEntity> getAll() {
        try {
            return repository.findAll();
        } catch (Exception e) {
            log.error("[ArticlesServiceImpl] >> getAll failed", e);
            throw new ApiException("500", "Failed to fetch all articles");
        }
    }

    @Override
    public Optional<ArticleDetailDTO> getById(Long id) {
        try {
            return repository.findById(id)
                    .map(ArticleMapper::toDetailDTO);
        } catch (Exception e) {
            log.error("[ArticlesServiceImpl] >> getById failed for id: {}", id, e);
            throw new ApiException("500", "Failed to fetch article by ID");
        }
    }

    @Override
    public ArticlesEntity save(ArticlesEntity article) {
        try {
            return repository.save(article);
        } catch (Exception e) {
            log.error("[ArticlesServiceImpl] >> save failed", e);
            throw new ApiException("500", "Failed to save article");
        }
    }

    @Override
    public List<ArticlesEntity> search(String keyword) {
        try {
            return repository.searchByKeyword(keyword);
        } catch (Exception e) {
            log.error("[ArticlesServiceImpl] >> search failed for keyword: {}", keyword, e);
            throw new ApiException("500", "Failed to search articles");
        }
    }

    @Override
    public List<ArticlesEntity> findByDate(LocalDate date) {
        try {
            return repository.findByAddedDate(date);
        } catch (Exception e) {
            log.error("[ArticlesServiceImpl] >> findByDate failed for date: {}", date, e);
            throw new ApiException("500", "Failed to fetch articles by date");
        }
    }

    @Override
    public List<ArticlesEntity> findTopArticles() {
        try {
            return repository.findByFeaturedTrue();
        } catch (Exception e) {
            log.error("[ArticlesServiceImpl] >> findTopArticles failed", e);
            throw new ApiException("500", "Failed to fetch top articles");
        }
    }

    @Override
    @Transactional
    public void incrementArticleCounts(Map<String, List<Long>> payload) {
        try {
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

        } catch (Exception e) {
            log.error("[ArticlesServiceImpl] >> incrementArticleCounts failed", e);
            throw new ApiException("500", "Failed to update article counts");
        }
    }
}
