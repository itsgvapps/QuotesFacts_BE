package com.gvapps.quotesfacts.service.test;

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
import java.util.*;
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
            InputStream inputStream = new ClassPathResource("temp/articles_life_901_1000.json").getInputStream();

            // Read JSON as List<Map>
            List<Map<String, Object>> jsonList = objectMapper.readValue(inputStream, new TypeReference<>() {
            });

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

            articlesRepository.saveAll(entities);
            log.info("✅ Successfully imported {} new articles.", newEntities.size());

        } catch (Exception e) {
            log.error("❌ Failed to import articles: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public void importInsertUpdateArticlesFromJson() {
        try {
            // Load JSON file from resources/temp
            InputStream inputStream =
                    new ClassPathResource("temp/articles_life_1_100.json").getInputStream();

            // Read JSON list
            List<Map<String, Object>> jsonList =
                    objectMapper.readValue(inputStream, new TypeReference<>() {
                    });

            // Convert → Entities
            List<ArticlesEntity> incomingEntities = jsonList.stream()
                    .map(this::mapToEntity)
                    .filter(Objects::nonNull)
                    .filter(e -> e.getTitle() != null && !e.getTitle().trim().isEmpty())
                    .collect(Collectors.toList());

            if (incomingEntities.isEmpty()) {
                log.info("⚠️ No valid articles found in JSON.");
                return;
            }

            // Fetch existing IDs + titles in one shot
            List<ArticlesEntity> existingList = articlesRepository.findAll();

            Map<Long, ArticlesEntity> existingById = existingList.stream()
                    .filter(e -> e.getId() != null)
                    .collect(Collectors.toMap(
                            ArticlesEntity::getId,
                            e -> e,
                            (a, b) -> a
                    ));

            Map<String, ArticlesEntity> existingByTitle = existingList.stream()
                    .filter(e -> e.getTitle() != null)
                    .collect(Collectors.toMap(
                            ArticlesEntity::getTitle,
                            e -> e,
                            (a, b) -> a
                    ));

            List<ArticlesEntity> toSave = new ArrayList<>();

            for (ArticlesEntity incoming : incomingEntities) {

                ArticlesEntity match = existingById.get(incoming.getId());

                // if ID doesn't match, try title match
                if (match == null && incoming.getTitle() != null) {
                    match = existingByTitle.get(incoming.getTitle());
                }

                if (match == null) {
                    // New article → insert
                    toSave.add(incoming);
                } else {
                    // Existing article → update fields
                    updateExistingArticle(match, incoming);
                    toSave.add(match);
                }
            }

            if (!toSave.isEmpty()) {
                articlesRepository.saveAll(toSave);
                log.info("✅ Saved {} records (inserted + updated).", toSave.size());
            } else {
                log.info("No records to save.");
            }

        } catch (Exception e) {
            log.error("❌ Failed to import or merge articles: {}", e.getMessage(), e);
        }
    }


    @Transactional
    public void updateSubTitleFromJson() {
        try {
            // Load JSON file from resources/temp
            InputStream inputStream = new ClassPathResource("temp/articles_life_201_300.json").getInputStream();

            // Read JSON as List<Map>
            List<Map<String, Object>> jsonList = objectMapper.readValue(inputStream, new TypeReference<>() {
            });


            if (jsonList == null || jsonList.isEmpty()) {
                log.info("⚠️ JSON file empty — nothing to update");
                return;
            }

            int updatedCount = 0;

            // 3. Loop each record and update directly based on ID
            for (Map<String, Object> item : jsonList) {

                Long id = item.get("id") == null ? null :
                        Long.valueOf(item.get("id").toString());

                String header = item.get("header") == null ? null :
                        item.get("header").toString().trim();

                // skip invalid
                if (id == null || header == null || header.isEmpty()) {
                    continue;
                }

                // 4. Find entity
                Optional<ArticlesEntity> optional = articlesRepository.findById(id);
                if (optional.isEmpty()) {
                    log.warn("❗ ID {} not found in DB. Skipping.", id);
                    continue;
                }

                ArticlesEntity entity = optional.get();

                // 5. Update field
                entity.setSubTitle(header);
                updatedCount++;
            }

            log.info("✅ Updated sub_title for {} articles.", updatedCount);

        } catch (Exception e) {
            log.error("❌ Failed to update sub_title: {}", e.getMessage(), e);
        }
    }


    private ArticlesEntity mapToEntity(Map<String, Object> json) {
        try {
            Object jsonId = json.get("id");
            Long parsedId = ((Number) jsonId).longValue();
            return ArticlesEntity.builder()
                    .id(parsedId)
                    .title((String) json.getOrDefault("title", null))
                    .subTitle((String) json.getOrDefault("header", ""))
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
                    .header("")
                    .active((boolean) json.getOrDefault("isShow", true))
                    .featured(false)
                    .readingTime(3)
                    .notes((String) json.getOrDefault("notes", ""))
                    .build();
        } catch (Exception e) {
            log.error("⚠️ Error mapping article: {}", e.getMessage());
            return null;
        }
    }

    private void updateExistingArticle(ArticlesEntity existing, ArticlesEntity incoming) {
        existing.setTitle(incoming.getTitle());
        existing.setSubTitle(incoming.getSubTitle());
        existing.setContent(incoming.getContent());
        existing.setSummary(incoming.getSummary());
        existing.setSource(incoming.getSource());
        existing.setAuthor(incoming.getAuthor());
        existing.setImgCredit(incoming.getImgCredit());
        existing.setType(incoming.getType());
        existing.setLikes(incoming.getLikes());
        existing.setNotes(incoming.getNotes());
        existing.setHeader(incoming.getHeader());
        existing.setActive(incoming.isActive());
        existing.setImgPath(incoming.getImgPath());
        existing.setTags(incoming.getTags());
    }

}
