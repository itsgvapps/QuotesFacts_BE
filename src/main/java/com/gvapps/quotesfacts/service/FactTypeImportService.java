package com.gvapps.quotesfacts.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gvapps.quotesfacts.entity.FactDetailsEntity;
import com.gvapps.quotesfacts.entity.FactTypeEntity;
import com.gvapps.quotesfacts.repository.FactDetailsRepository;
import com.gvapps.quotesfacts.repository.FactTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FactTypeImportService {

    private final FactTypeRepository factTypeRepository;
    private final FactDetailsRepository factDetailsRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void importFactTypesFromJson() {
        try (InputStream inputStream = getClass().getResourceAsStream("/temp/FACT_TYPE.json")) {
            if (inputStream == null) {
                throw new RuntimeException("File not found: FACT_TYPE.json");
            }

            // Read the JSON array into a List of generic maps
            List<Map<String, Object>> factTypeList = objectMapper.readValue(
                    inputStream,
                    new TypeReference<>() {}
            );

            List<FactTypeEntity> entities = factTypeList.stream()
                    // ✅ Filter only rows where TYPE_ID == 22
                    .filter(item -> getLong(item.get("TYPE_ID")) != null && getLong(item.get("TYPE_ID")) == 22L)

                    // ✅ Sort by CATEGORY_ID (ascending order)
                    .sorted(Comparator.comparing(item -> getLong(item.get("CATEGORY_ID"))))

                    // ✅ Map JSON fields to entity
                    .map(item -> {
                        FactTypeEntity entity = new FactTypeEntity();

                        // Map JSON fields
                        entity.setId(Math.toIntExact(getLong(item.get("CATEGORY_ID"))));
                        entity.setTypeId(Math.toIntExact(getLong(item.get("TYPE_ID"))));
                        entity.setType(getString(item.get("TYPE_NAME")));
                        entity.setName(getString(item.get("CATEGORY_NAME")));

                        // Defaults
                        entity.setShortDescription(getString(item.get("SHORT_DESCRIPTION")));
                        entity.setDescription(getString(item.get("DESCRIPTION")));
                        entity.setActive(true);
                        entity.setFeatured(false);
                        entity.setViews(0);

                        // Image / Icon placeholders
                        entity.setIcon("");
                        entity.setImage("");
                        entity.setLargeImage("");
                        entity.setPreviewImage("");
                        entity.setVerticalImage("");
                        entity.setVerticalImageWithText("");
                        entity.setHorizontalImage("");
                        entity.setHorizontalImageWithText("");
                        entity.setExternalUrl("");

                        // Tags
                        entity.setTags("[\"" + entity.getName().toLowerCase() + "\"]");

                        // Dates
                        entity.setAddedDate(LocalDate.now());
                        entity.setUpdatedDate(LocalDate.now());

                        return entity;
                    })
                    .collect(Collectors.toList());

            factTypeRepository.saveAll(entities);
            System.out.println("✅ Successfully imported " + entities.size() + " Fact Types from JSON!");

        } catch (Exception e) {
            throw new RuntimeException("❌ Error reading or inserting FACT_TYPE.json: " + e.getMessage(), e);
        }
    }

    public void importFactDetailsFromJson() {
        try (InputStream inputStream = getClass().getResourceAsStream("/temp/FACT_DETAILS.json")) {
            if (inputStream == null) {
                throw new RuntimeException("File not found: FACT_DETAILS.json");
            }

            // Read JSON array into a list of maps
            List<Map<String, Object>> factList = objectMapper.readValue(
                    inputStream,
                    new TypeReference<>() {}
            );

            List<FactDetailsEntity> entities = factList.stream()

                    // ✅ Sort by CATEGORY_ID (ascending order)
                    .sorted(Comparator.comparing(item -> getLong(item.get("CATEGORY_ID"))))

                    // ✅ Map JSON fields to FactDetailsEntity
                    .map(item -> {
                        FactDetailsEntity entity = new FactDetailsEntity();

                        // --- Map values from JSON ---
                        entity.setCategoryId(Math.toIntExact(getLong(item.get("CATEGORY_ID"))));
                        entity.setText(getString(item.get("DESCRIPTION")));

                        // --- Default values (as per DB defaults) ---
                        entity.setShortSummary(null);
                        entity.setLongSummary(null);
                        entity.setArticleId(0);
                        entity.setSourceUrl("[]");
                        entity.setLikes(0);
                        entity.setBookmarks(0);
                        entity.setDownloads(0);
                        entity.setShares(0);
                        entity.setViews(0);
                        entity.setTags("[]");
                        entity.setVerified(false);
                        entity.setLanguage("en");
                        entity.setAddedDate(LocalDate.now());
                        entity.setUpdatedDate(LocalDate.now());

                        return entity;
                    })
                    .collect(Collectors.toList());

            factDetailsRepository.saveAll(entities);
            System.out.println("✅ Successfully imported " + entities.size() + " Fact Details from JSON!");

        } catch (Exception e) {
            throw new RuntimeException("❌ Error reading or inserting FACT_DETAILS.json: " + e.getMessage(), e);
        }
    }

    // --- Helper methods ---
    private static String getString(Object o) {
        return o != null ? o.toString() : "";
    }

    private static Long getLong(Object o) {
        if (o == null) return null;
        if (o instanceof Number) return ((Number) o).longValue();
        try {
            return Long.parseLong(o.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
