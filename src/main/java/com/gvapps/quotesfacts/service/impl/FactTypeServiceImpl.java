package com.gvapps.quotesfacts.service.impl;

import com.gvapps.quotesfacts.entity.ArticlesEntity;
import com.gvapps.quotesfacts.entity.FactDetailsEntity;
import com.gvapps.quotesfacts.entity.FactTypeEntity;
import com.gvapps.quotesfacts.repository.ArticlesRepository;
import com.gvapps.quotesfacts.repository.FactDetailsRepository;
import com.gvapps.quotesfacts.repository.FactTypeRepository;
import com.gvapps.quotesfacts.service.FactTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FactTypeServiceImpl implements FactTypeService {

    private final FactDetailsRepository factDetailsRepository;
    private final FactTypeRepository factTypeRepository;
    private final ArticlesRepository articlesRepository;

    @Override
    public List<FactTypeEntity> getAllCategories() {
        return factTypeRepository.findAll();
    }

    @Override
    public List<FactTypeEntity> getCategoriesByTypeId(int typeId) {
        return factTypeRepository.findByTypeIdAndActiveTrue(typeId);
    }

    @Transactional
    public void incrementCategoryViewsAsync(Map<String, List<Long>> payload) {

        if (payload.containsKey("views")) {
            List<Long> ids = payload.get("views");
            factTypeRepository.incrementViewsByCategoryIds(ids);
        }
    }

    @Override
    public Map<String, Object> getHomeTabData() {
        log.info("[FactTypeServiceImpl] >> Fetching Home tab data");

        Map<String, Object> dashboard = new LinkedHashMap<>();

        // 1️⃣ Today's Random 5 Facts (shorter than 160 chars)
        List<FactDetailsEntity> todayFacts = factDetailsRepository.findRandomShortFacts();
        Map<String, Object> factsSection = new HashMap<>();
        factsSection.put("header", Map.of(
                "title", "Today’s Quick Facts",
                "subTitle", "Short, snappy, and verified",
                "gradientColors", List.of("#FEF9C3", "#E0F2FE", "#E9D5FF")
        ));
        factsSection.put("items", todayFacts);
        dashboard.put("todayFacts", factsSection);

        // 2️⃣ Popular Fact Categories
        List<FactTypeEntity> popularCategories = factTypeRepository.findTop4PopularCategories();
        Map<String, Object> categoriesSection = new HashMap<>();
        categoriesSection.put("header", Map.of(
                "title", "Popular Fact Categories",
                "subTitle", "Discover trending topics and curiosities"
        ));
        categoriesSection.put("items", popularCategories);
        dashboard.put("popularCategories", categoriesSection);

        // 3️⃣ Top 5 Articles by Type
        List<ArticlesEntity> topArticles = articlesRepository.findTop5Articles();
        Map<String, Object> articlesSection = new HashMap<>();
        articlesSection.put("header", Map.of(
                "title", "Featured Articles",
                "subTitle", "In-depth stories & curated facts"
        ));
        articlesSection.put("items", topArticles);
        dashboard.put("topArticles", articlesSection);

        return dashboard;
    }

    @Override
    public Map<String, Object> getDiscoverTabData() {
        log.info("[FactTypeServiceImpl] >> Fetching Discover tab data");

        Map<String, Object> discover = new LinkedHashMap<>();

        // 1️⃣ Facts by Category (FactTypeEntity, type_id = 11)
        List<FactTypeEntity> factsByCategory = factTypeRepository.findTopByTypeIdAndActiveTrue(11, 4);
        Map<String, Object> factsByCategorySection = new HashMap<>();
        factsByCategorySection.put("header", Map.of(
                "title", "Facts by Category",
                "subTitle", "Explore diverse fact types",
                "gradientColors", List.of("#FDE68A", "#FECACA", "#E0F2FE")
        ));
        factsByCategorySection.put("items", factsByCategory);
        discover.put("SECTION_EXPLORE_TEXT_CATEGORIES_GRID_1", factsByCategorySection);

        // 2️⃣ Interesting Facts (ArticlesEntity, tag = "Quotes")
        List<ArticlesEntity> interestingQuotes = articlesRepository.findTopByTag("Quotes", 4);
        Map<String, Object> quotesSection = new HashMap<>();
        quotesSection.put("header", Map.of(
                "title", "Interesting Facts",
                "subTitle", "Timeless and must read facts",
                "gradientColors", List.of("#BBF7D0", "#FDE68A", "#E0F2FE")
        ));
        quotesSection.put("items", interestingQuotes);
        discover.put("SECTION_EXPLORE_ARTICLE_ITEMS_VERTICAL_SMALL_1", quotesSection);

        // 3️⃣ Mind-Blowing Psychology Facts (FactTypeEntity, type_id = 22)
        List<FactTypeEntity> psychologyFacts = factTypeRepository.findTopByTypeIdAndActiveTrue(22, 4);
        Map<String, Object> psychologySection = new HashMap<>();
        psychologySection.put("header", Map.of(
                "title", "Mind-Blowing Psychology Facts",
                "subTitle", "Explore fascinating mind insights",
                "gradientColors", List.of("#E9D5FF", "#C7D2FE", "#A5F3FC")
        ));
        psychologySection.put("items", psychologyFacts);
        discover.put("SECTION_EXPLORE_TEXT_CATEGORIES_GRID_LONG_TEXT_1", psychologySection);

        // 4️⃣ Curious Reads (ArticlesEntity, tag = "Psychology")
        List<ArticlesEntity> curiousReads = articlesRepository.findTopByTag("Psychology", 4);
        Map<String, Object> curiousReadsSection = new HashMap<>();
        curiousReadsSection.put("header", Map.of(
                "title", "Curious Reads",
                "subTitle", "Thought-provoking psychology reads",
                "gradientColors", List.of("#FECACA", "#E0F2FE", "#BBF7D0")
        ));
        curiousReadsSection.put("items", curiousReads);
        discover.put("curiousReads", curiousReadsSection);

        log.info("[FactTypeServiceImpl] ✅ Discover data ready with {} sections", discover.size());
        return discover;
    }

    @Override
    public List<FactTypeEntity> getTopPopularCategories(int limit) {
        List<FactTypeEntity> popular = factTypeRepository.findTop4ByOrderByViewsDesc();
        return popular.size() > limit ? popular.subList(0, limit) : popular;
    }

    @Override
    public List<FactTypeEntity> getTopTrendingCategories(int limit) {
        List<FactTypeEntity> trending = factTypeRepository.findTop4ByOrderByUpdatedDateDesc();
        return trending.size() > limit ? trending.subList(0, limit) : trending;
    }
}
