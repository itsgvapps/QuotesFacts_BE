package com.gvapps.quotesfacts.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gvapps.quotesfacts.entity.FactDetailsEntity;
import com.gvapps.quotesfacts.entity.FactTypeEntity;
import com.gvapps.quotesfacts.repository.FactDetailsRepository;
import com.gvapps.quotesfacts.repository.FactTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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
                    // ✅ Filter only rows where type_id == 22 (updated from your sample)
                    .filter(item -> getLong(item.get("type_id")) != null && getLong(item.get("type_id")) == 22L)

                    // ✅ Sort by category_id (ascending order)
                    .sorted(Comparator.comparing(item -> getLong(item.get("category_id"))))

                    // ✅ Map JSON fields to entity
                    .map(item -> {
                        FactTypeEntity entity = new FactTypeEntity();

                        // Map JSON fields - using proper type conversion
                        Long categoryId = getLong(item.get("category_id"));
                        Long typeId = getLong(item.get("type_id"));

                        entity.setId(categoryId != null ? Math.toIntExact(categoryId) : 0);
                        entity.setTypeId(typeId != null ? Math.toIntExact(typeId) : 0);
                        entity.setType(getString(item.get("type")));
                        entity.setName(getString(item.get("name")));

                        // Map description fields
                        entity.setShortDescription(getString(item.get("short_description")));
                        entity.setDescription(getString(item.get("description")));

                        // Map boolean fields
                        entity.setActive(getBoolean(item.get("active")));
                        entity.setFeatured(getBoolean(item.get("featured")));

                        // Map numeric fields with null safety
                        entity.setViews(getLong(item.get("views")) != null ? Math.toIntExact(getLong(item.get("views"))) : 0);

                        // Image / Icon fields
                        entity.setIcon(getString(item.get("icon")));
                        entity.setImage(getString(item.get("image")));
                        entity.setLargeImage(getString(item.get("large_image")));
                        entity.setPreviewImage(getString(item.get("preview_image")));
                        entity.setVerticalImage(getString(item.get("vertical_image")));
                        entity.setVerticalImageWithText(getString(item.get("vertical_image_with_text")));
                        entity.setHorizontalImage(getString(item.get("horizontal_image")));
                        entity.setHorizontalImageWithText(getString(item.get("horizontal_image_with_text")));
                        entity.setExternalUrl(getString(item.get("external_url")));

                        // Tags - use existing tags or generate from name
                        String tags = getString(item.get("tags"));
                        if (tags.isEmpty()) {
                            entity.setTags("[\"" + entity.getName().toLowerCase() + "\"]");
                        } else {
                            entity.setTags(tags);
                        }

                        // Dates - parse from JSON or use current date
                        String addedDateStr = getString(item.get("added_date"));
                        String updatedDateStr = getString(item.get("updated_date"));

                        entity.setAddedDate(addedDateStr != null && !addedDateStr.isEmpty()
                                ? LocalDate.parse(addedDateStr)
                                : LocalDate.now());
                        entity.setUpdatedDate(updatedDateStr != null && !updatedDateStr.isEmpty()
                                ? LocalDate.parse(updatedDateStr)
                                : LocalDate.now());

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
                    new TypeReference<>() {
                    }
            );

            List<FactDetailsEntity> entities = factList.stream()
                    // ✅ Sort by CATEGORY_ID (ascending order)
                    .sorted(Comparator.comparing(item -> getLong(item.get("CATEGORY_ID")), Comparator.nullsLast(Long::compareTo)))

                    // ✅ Map JSON fields to FactDetailsEntity
                    .map(item -> {
                        FactDetailsEntity entity = new FactDetailsEntity();

                        // 🔹 Convert category_id safely (Long → int)
                        Long categoryId = getLong(item.get("CATEGORY_ID"));
                        entity.setCategoryId(categoryId != null ? categoryId.intValue() : 0);

                        // 🔹 Optional id if needed
                        //Long id = getLong(item.get("id"));
                        //if (id != null) entity.setId(id.intValue());

                        // 🔹 Text / Description fallback
                        String text = getString(item.get("DESCRIPTION"));
                        entity.setText(text);

                        // 🔹 Date handling
                        entity.setAddedDate(parseDateOrNow(getString(item.get("added_date"))));
                        entity.setUpdatedDate(parseDateOrNow(getString(item.get("updated_date"))));

                        return entity;
                    })
                    .collect(Collectors.toList());

            factDetailsRepository.saveAll(entities);
            System.out.println("✅ Successfully imported " + entities.size() + " Fact Details from JSON!");

        } catch (Exception e) {
            throw new RuntimeException("❌ Error reading or inserting FACT_DETAILS.json: " + e.getMessage(), e);
        }
    }

    public void importFactDetailsFromJson1() {
        try (InputStream inputStream = getClass().getResourceAsStream("/temp/factdetails.json")) {
            if (inputStream == null) {
                throw new RuntimeException("File not found: factdetails.json");
            }

            // Read JSON array into a list of maps
            List<Map<String, Object>> factList = objectMapper.readValue(
                    inputStream,
                    new TypeReference<>() {}
            );

            List<FactDetailsEntity> entities = factList.stream()
                    // ✅ Sort by category_id (ascending order)
                    .sorted(Comparator.comparing(item -> getLong(item.get("category_id")), Comparator.nullsLast(Long::compareTo)))

                    // ✅ Map JSON fields to FactDetailsEntity
                    .map(item -> {
                        FactDetailsEntity entity = new FactDetailsEntity();

                        // 🔹 Convert category_id safely (Long → int)
                        Long categoryId = getLong(item.get("category_id"));
                        entity.setCategoryId(categoryId != null ? categoryId.intValue() : 0);

                        // 🔹 Optional id if needed
                        Long id = getLong(item.get("id"));
                        if (id != null) entity.setId(id.intValue());

                        // 🔹 Text / Description fallback
                        String text = getString(item.get("text"));
                        entity.setText(text);

                        entity.setShortSummary(getString(item.get("short_summary")));
                        entity.setLongSummary(getString(item.get("long_summary")));

                        // 🔹 Numeric fields
                        entity.setArticleId(getInt(item.get("article_id")));
                        entity.setLikes(getInt(item.get("likes")));
                        entity.setBookmarks(getInt(item.get("bookmarks")));
                        entity.setDownloads(getInt(item.get("downloads")));
                        entity.setShares(getInt(item.get("shares")));
                        entity.setViews(getInt(item.get("views")));

                        // 🔹 Strings (JSON might have "[]" for empty)
                        entity.setSourceUrl(cleanString(getString(item.get("source_url"))));
                        entity.setTags(cleanString(getString(item.get("tags"))));
                        entity.setLanguage(getString(item.get("language")));
                        //entity.setExternalUrl(getString(item.get("external_url")));

                        // 🔹 Convert 0/1 to boolean safely
                        entity.setVerified(getBooleanFlexible(item.get("verified")));

                        // 🔹 Date handling
                        entity.setAddedDate(parseDateOrNow(getString(item.get("added_date"))));
                        entity.setUpdatedDate(parseDateOrNow(getString(item.get("updated_date"))));

                        return entity;
                    })
                    .collect(Collectors.toList());

            factDetailsRepository.saveAll(entities);
            System.out.println("✅ Successfully imported " + entities.size() + " Fact Details from JSON!");

        } catch (Exception e) {
            throw new RuntimeException("❌ Error reading or inserting factdetails.json: " + e.getMessage(), e);
        }
    }


    // --- Improved Helper methods ---

    private static Boolean getBoolean(Object o) {
        if (o == null) return false; // Default to false for null values
        if (o instanceof Boolean) return (Boolean) o;
        if (o instanceof Number) return ((Number) o).intValue() != 0;
        if (o instanceof String) {
            String str = ((String) o).toLowerCase();
            return str.equals("true") || str.equals("1") || str.equals("yes");
        }
        return false;
    }

    private Long getLong(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).longValue();
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer getInt(Object value) {
        Long val = getLong(value);
        return val != null ? val.intValue() : 0;
    }

    private String getString(Object value) {
        return value != null ? value.toString().trim() : "";
    }

    // Clean up placeholder values like "[]" or "null"
    private String cleanString(String s) {
        if (s == null) return "";
        return (s.equalsIgnoreCase("[]") || s.equalsIgnoreCase("null")) ? "" : s.trim();
    }

    // Flexible boolean conversion: handles 0/1, "true"/"false"
    private Boolean getBooleanFlexible(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        String s = value.toString().trim().toLowerCase();
        return s.equals("1") || s.equals("true") || s.equals("yes");
    }

    // Parse LocalDate or return today if blank
    private LocalDate parseDateOrNow(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return LocalDate.now();
        try {
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }

}