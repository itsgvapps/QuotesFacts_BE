package com.gvapps.quotesfacts.service.impl;

import com.gvapps.quotesfacts.entity.ArticlesEntity;
import com.gvapps.quotesfacts.entity.FactDetailsEntity;
import com.gvapps.quotesfacts.entity.FactTypeEntity;
import com.gvapps.quotesfacts.exception.ApiException;
import com.gvapps.quotesfacts.repository.ArticlesRepository;
import com.gvapps.quotesfacts.repository.FactDetailsRepository;
import com.gvapps.quotesfacts.repository.FactTypeRepository;
import com.gvapps.quotesfacts.service.FactTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FactTypeServiceImpl implements FactTypeService {

    private final FactDetailsRepository factDetailsRepository;
    private final FactTypeRepository factTypeRepository;
    private final ArticlesRepository articlesRepository;

    // ----------------------------------------------------------------------------------------
    // CATEGORY MANAGEMENT
    // ----------------------------------------------------------------------------------------

    @Override
    public List<FactTypeEntity> getAllCategories() {
        try {
            return factTypeRepository.findAll();
        } catch (Exception e) {
            log.error("[getAllCategories] ❌ Failed to fetch categories", e);
            throw new ApiException("500", "Failed to fetch categories");
        }
    }

    @Override
    public List<FactTypeEntity> getCategoriesByTypeId(int typeId) {
        try {
            List<FactTypeEntity> categories = factTypeRepository.findByTypeIdAndActiveTrue(typeId);
            if (categories.isEmpty()) {
                throw new ApiException("404", "No categories found for typeId: " + typeId);
            }
            return categories;
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("[getCategoriesByTypeId] ❌ Failed for typeId={}", typeId, e);
            throw new ApiException("500", "Error retrieving categories for typeId=" + typeId);
        }
    }

    @Override
    public List<FactTypeEntity> getTopPopularCategories(int limit) {
        log.info("[getTopPopularCategories] >> Fetching top {} popular categories", limit);

        try {
            if (limit <= 0) {
                throw new ApiException("400", "Limit must be greater than zero");
            }

            List<FactTypeEntity> popular = factTypeRepository.findTop4ByOrderByViewsDesc();

            if (popular == null || popular.isEmpty()) {
                log.warn("[getTopPopularCategories] ⚠️ No popular categories found");
                return Collections.emptyList();
            }

            // Prevent IndexOutOfBoundsException
            int safeLimit = Math.min(limit, popular.size());
            List<FactTypeEntity> result = popular.subList(0, safeLimit);

            log.info("[getTopPopularCategories] ✅ Returning {} categories", result.size());
            return result;

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("[getTopPopularCategories] ❌ Error retrieving popular categories", e);
            throw new ApiException("500", "Failed to fetch popular categories");
        }
    }

    @Override
    public List<FactTypeEntity> getTopTrendingCategories(int limit) {
        log.info("[getTopTrendingCategories] >> Fetching top {} trending categories", limit);

        try {
            if (limit <= 0) {
                throw new ApiException("400", "Limit must be greater than zero");
            }

            List<FactTypeEntity> trending = factTypeRepository.findTop4ByOrderByUpdatedDateDesc();

            if (trending == null || trending.isEmpty()) {
                log.warn("[getTopTrendingCategories] ⚠️ No trending categories found");
                return Collections.emptyList();
            }

            int safeLimit = Math.min(limit, trending.size());
            List<FactTypeEntity> result = trending.subList(0, safeLimit);

            log.info("[getTopTrendingCategories] ✅ Returning {} categories", result.size());
            return result;

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("[getTopTrendingCategories] ❌ Error retrieving trending categories", e);
            throw new ApiException("500", "Failed to fetch trending categories");
        }
    }


    // ✅ Run view increments asynchronously (non-blocking)
    @Async("taskExecutor")
    @Transactional
    public void incrementCategoryViewsAsync(Map<String, List<Long>> payload) {
        try {
            if (!payload.containsKey("views")) {
                log.warn("[incrementCategoryViewsAsync] ⚠️ Missing 'views' key");
                return;
            }

            List<Long> ids = payload.get("views");
            if (ids == null || ids.isEmpty()) {
                log.warn("[incrementCategoryViewsAsync] ⚠️ Empty ID list");
                return;
            }

            factTypeRepository.incrementViewsByCategoryIds(ids);
            log.info("[incrementCategoryViewsAsync] ✅ Incremented views for categories: {}", ids);

        } catch (Exception e) {
            // Don't rethrow — async tasks should fail silently but log clearly
            log.error("[incrementCategoryViewsAsync] ❌ Error incrementing category views", e);
        }
    }

    // ----------------------------------------------------------------------------------------
    // HOME & DISCOVER DASHBOARD
    // ----------------------------------------------------------------------------------------

    @Override
    public Map<String, Object> getHomeTabData() {
        log.info("[getHomeTabData] >> Fetching Home tab data");
        Map<String, Object> dashboard = new LinkedHashMap<>();

        try {
            List<FactDetailsEntity> todayFacts = factDetailsRepository.findRandomShortFacts();
            dashboard.put("SECTION_HOME_TODAY_PICKS", Map.of(
                    "header", Map.of(
                            "title", "Today’s Quick Facts",
                            "subTitle", "Short, snappy, and verified",
                            "gradientColors", List.of("#FEF9C3", "#E0F2FE", "#E9D5FF")
                    ),
                    "items", todayFacts
            ));

            List<FactTypeEntity> popularCategories = factTypeRepository.findTop4PopularCategories();
            dashboard.put("SECTION_HOME_TEXT_CATEGORY_GRID_LONG_TEXT_1", Map.of(
                    "header", Map.of(
                            "title", "Popular Fact Categories",
                            "subTitle", "Discover trending topics and curiosities"
                    ),
                    "items", popularCategories
            ));

            List<ArticlesEntity> topArticles = articlesRepository.findRandomArticlesByTag("Psychology", 4);
            dashboard.put("SECTION_HOME_ARTICLES_TALL_CARD_1", Map.of(
                    "header", Map.of(
                            "title", "Featured Articles",
                            "subTitle", "In-depth stories & curated facts"
                    ),
                    "items", topArticles
            ));

            dashboard.put("SECTION_HOME_TEXT_CATEGORY_GRID_SHORT_CARD_1", Map.of(
                    "header", Map.of(
                            "title", "Facts by Category",
                            "subTitle", "Explore diverse fact types"
                    ),
                    "items", factTypeRepository.findTopByTypeIdAndActiveTrue(11, 4)
            ));

            dashboard.put("SECTION_HOME_ARTICLES_GRID_1", Map.of(
                    "header", Map.of(
                            "title", "Featured Facts",
                            "subTitle", "Timeless and must-read facts"
                    ),
                    "items", articlesRepository.findRandomArticlesByTag("Lifestyle", 4)
            ));

            dashboard.put("SECTION_HOME_ARTICLES_SHORT_CARD_1", Map.of(
                    "header", Map.of(
                            "title", "Curious Reads",
                            "subTitle", "Thought-provoking psychology reads"
                    ),
                    "items", articlesRepository.findTopArticles(4)
            ));


            log.info("[getHomeTabData] ✅ Home dashboard ready with {} sections", dashboard.size());
            return dashboard;

        } catch (Exception e) {
            log.error("[getHomeTabData] ❌ Error building home dashboard", e);
            throw new ApiException("500", "Failed to build home dashboard");
        }
    }

    @Override
    public Map<String, Object> getDiscoverTabData() {
        log.info("[getDiscoverTabData] >> Fetching Discover tab data");

        Map<String, Object> discover = new LinkedHashMap<>();
        try {
            discover.put("SECTION_EXPLORE_TEXT_CATEGORY_GRID_TALL_CARD_1", Map.of(
                    "header", Map.of(
                            "title", "Facts by Category",
                            "subTitle", "Explore diverse fact types"
                    ),
                    "items", factTypeRepository.findTopByTypeIdAndActiveTrue(11, 4)
            ));

            discover.put("SECTION_EXPLORE_ARTICLES_SHORT_CARD_1", Map.of(
                    "header", Map.of(
                            "title", "Curious Reads",
                            "subTitle", "Thought-provoking psychology reads"
                    ),
                    "items", articlesRepository.findRandomArticlesByTag("Psychology", 4)
            ));

            discover.put("SECTION_EXPLORE_ARTICLES_GRID_1", Map.of(
                    "header", Map.of(
                            "title", "Featured Facts",
                            "subTitle", "Timeless and must-read facts"
                    ),
                    "items", articlesRepository.findRandomArticlesByTag("Lifestyle", 4)
            ));

            discover.put("SECTION_EXPLORE_TEXT_CATEGORY_GRID_LONG_NAME_1", Map.of(
                    "header", Map.of(
                            "title", "Mind-Blowing Psychology Facts",
                            "subTitle", "Explore fascinating mind insights"
                    ),
                    "items", factTypeRepository.findTopByTypeIdAndActiveTrue(22, 4)
            ));

            log.info("[getDiscoverTabData] ✅ Discover tab ready with {} sections", discover.size());
            return discover;

        } catch (Exception e) {
            log.error("[getDiscoverTabData] ❌ Error building discover tab", e);
            throw new ApiException("500", "Failed to build discover tab");
        }
    }

    // ----------------------------------------------------------------------------------------
    // FACT DETAILS MANAGEMENT
    // ----------------------------------------------------------------------------------------

    @Override
    public FactDetailsEntity saveFact(FactDetailsEntity fact) {
        try {
            return factDetailsRepository.save(fact);
        } catch (Exception e) {
            log.error("[saveFact] ❌ Failed to save fact", e);
            throw new ApiException("500", "Failed to save fact");
        }
    }

    @Override
    public Optional<FactDetailsEntity> getFactById(int id) {
        return factDetailsRepository.findById(id);
    }

    @Override
    public List<FactDetailsEntity> getAllFacts() {
        return factDetailsRepository.findAll();
    }

    @Override
    public List<FactDetailsEntity> getFactsByCategory(int categoryId) {
        return factDetailsRepository.findRandomByCategoryId(categoryId, 100);
    }

    @Override
    public List<FactDetailsEntity> getFactsByLanguage(String language) {
        return factDetailsRepository.findByLanguage(language);
    }

    @Override
    public List<FactDetailsEntity> getVerifiedFacts() {
        return factDetailsRepository.findByVerifiedTrue();
    }

    @Override
    public List<FactDetailsEntity> getTopViewedFacts(int limit) {
        List<FactDetailsEntity> topFacts = factDetailsRepository.findTop10ByOrderByViewsDesc();
        return topFacts.subList(0, Math.min(limit, topFacts.size()));
    }

    @Override
    public List<FactDetailsEntity> searchFactsByKeyword(String keyword) {
        return factDetailsRepository.findByTextContainingIgnoreCase(keyword);
    }

    @Override
    public FactDetailsEntity updateFact(int id, FactDetailsEntity updatedFact) {
        try {
            return factDetailsRepository.findById(id)
                    .map(existingFact -> {
                        existingFact.setText(updatedFact.getText());
                        existingFact.setCategoryId(updatedFact.getCategoryId());
                        existingFact.setShortSummary(updatedFact.getShortSummary());
                        existingFact.setLongSummary(updatedFact.getLongSummary());
                        existingFact.setSourceUrl(updatedFact.getSourceUrl());
                        existingFact.setLikes(updatedFact.getLikes());
                        existingFact.setBookmarks(updatedFact.getBookmarks());
                        existingFact.setDownloads(updatedFact.getDownloads());
                        existingFact.setShares(updatedFact.getShares());
                        existingFact.setViews(updatedFact.getViews());
                        existingFact.setTags(updatedFact.getTags());
                        existingFact.setVerified(updatedFact.isVerified());
                        existingFact.setLanguage(updatedFact.getLanguage());
                        existingFact.setAddedDate(updatedFact.getAddedDate());
                        existingFact.setUpdatedDate(updatedFact.getUpdatedDate());
                        return factDetailsRepository.save(existingFact);
                    })
                    .orElseThrow(() -> new ApiException("404", "Fact not found with id " + id));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("[updateFact] ❌ Failed to update fact id={}", id, e);
            throw new ApiException("500", "Error updating fact");
        }
    }

    @Override
    public void deleteFact(int id) {
        try {
            factDetailsRepository.deleteById(id);
        } catch (Exception e) {
            log.error("[deleteFact] ❌ Failed to delete fact id={}", id, e);
            throw new ApiException("500", "Failed to delete fact");
        }
    }

    // ✅ Async non-blocking increment (e.g. called from API after viewing facts)
    @Async("taskExecutor")
    @Transactional
    public void incrementDetailCounts(Map<String, List<Long>> payload) {
        try {
            if (payload == null || payload.isEmpty()) {
                log.warn("[incrementDetailCounts] ⚠️ Empty payload");
                return;
            }

            if (payload.containsKey("views")) {
                List<Long> ids = payload.get("views");
                if (ids != null && !ids.isEmpty()) factDetailsRepository.incrementViews(ids);
            }
            if (payload.containsKey("likes")) {
                List<Long> ids = payload.get("likes");
                if (ids != null && !ids.isEmpty()) factDetailsRepository.incrementLikes(ids);
            }
            if (payload.containsKey("bookmarks")) {
                List<Long> ids = payload.get("bookmarks");
                if (ids != null && !ids.isEmpty()) factDetailsRepository.incrementBookmarks(ids);
            }
            if (payload.containsKey("downloads")) {
                List<Long> ids = payload.get("downloads");
                if (ids != null && !ids.isEmpty()) factDetailsRepository.incrementDownloads(ids);
            }
            if (payload.containsKey("shares")) {
                List<Long> ids = payload.get("shares");
                if (ids != null && !ids.isEmpty()) factDetailsRepository.incrementShares(ids);
            }

            log.info("[incrementDetailCounts] ✅ Incremented fact metrics: {}", payload.keySet());

        } catch (Exception e) {
            log.error("[incrementDetailCounts] ❌ Error incrementing fact metrics", e);
        }
    }
}
