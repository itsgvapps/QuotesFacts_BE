package com.gvapps.quotesfacts.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gvapps.quotesfacts.entity.ArticlesEntity;
import com.gvapps.quotesfacts.repository.ArticlesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticlesImportService {

    private final ArticlesRepository articlesRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void importArticlesFromJson() {
        try {
            // Load JSON file from resources/temp
            InputStream inputStream = new ClassPathResource("temp/articles_life_1_100.json").getInputStream();

            // Read JSON as List<Map>
            List<Map<String, Object>> jsonList = objectMapper.readValue(inputStream, new TypeReference<>() {});

            // Convert JSON objects → Entities
            List<ArticlesEntity> entities = jsonList.stream()
                    .map(this::mapToEntity)
                    .filter(Objects::nonNull)
                    .filter(e -> e.getTitle() != null && !e.getTitle().trim().isEmpty()) // ✅ skip empty titles
                    .collect(Collectors.toList());

            if (entities.isEmpty()) {
                log.info("⚠️ No valid articles found to import (all skipped or invalid).");
                return;
            }

            // Optional: filter out duplicates by title
            List<String> existingTitles = articlesRepository.findAll()
                    .stream()
                    .map(ArticlesEntity::getTitle)
                    .collect(Collectors.toList());

            List<ArticlesEntity> newEntities = entities.stream()
                    .filter(e -> !existingTitles.contains(e.getTitle()))
                    .collect(Collectors.toList());

            if (newEntities.isEmpty()) {
                log.info("✅ No new unique articles to import.");
                return;
            }

            articlesRepository.saveAll(newEntities);
            log.info("✅ Successfully imported {} new articles.", newEntities.size());

        } catch (Exception e) {
            log.error("❌ Failed to import articles: {}", e.getMessage(), e);
        }
    }

    private ArticlesEntity mapToEntity(Map<String, Object> json) {
        try {
            return ArticlesEntity.builder()
                    .title((String) json.getOrDefault("title", null))
                    .subTitle((String) json.getOrDefault("subTitle", ""))
                    .content((String) json.getOrDefault("content", ""))
                    .source((String) json.getOrDefault("src", json.get("source")))
                    .summary("")
                    .author((String) json.getOrDefault("author", ""))
                    .imgCredit((String) json.getOrDefault("imgSrc", json.get("imgCredit")))
                    .imgPath((String) json.getOrDefault("imgPath", ""))
                    .externalUrl((String) json.getOrDefault("externalUrl", ""))
                    .type((String) json.getOrDefault("type", "General"))
                    .categoryId("life")
                    .categoryName("Life")
                    .tags((List<String>) json.getOrDefault("tags", List.of("General")))
                    .addedDate(LocalDate.now())
                    .updatedDate(LocalDate.now())
                    .header((String) json.getOrDefault("header", ""))
                    .active((boolean) json.getOrDefault("isShow", true))
                    .featured(false)
                    .likes(0)
                    .views(0)
                    .downloads(0)
                    .favourites(0)
                    .bookmarks(0)
                    .readingTime(3)
                    .notes((String) json.getOrDefault("notes", ""))
                    .build();
        } catch (Exception e) {
            log.error("⚠️ Error mapping article: {}", e.getMessage());
            return null;
        }
    }
}
