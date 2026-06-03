package com.gvapps.quotesfacts.util;

import com.gvapps.quotesfacts.dto.FactImageResponse;
import com.gvapps.quotesfacts.entity.FactImageCategoryEntity;

import java.util.LinkedHashMap;
import java.util.Map;

public class FactImageBuilder {

    private FactImageBuilder() {
    }

    public static FactImageResponse build(FactImageCategoryEntity category, Long factId) {
        validateFactId(category, factId);

        long serialNumber = factId - category.getMinId() + 1;
        String fileName = category.getImagePrefix() + serialNumber + "." + category.getImageExtension();
        String basePath = removeTrailingSlash(category.getBaseFolder());

        Map<String, String> images = new LinkedHashMap<>();
        images.put("small", basePath + "/" + category.getSmallImageFolder() + "/" + fileName);
        images.put("medium", basePath + "/" + category.getMediumImageFolder() + "/" + fileName);
        images.put("large", basePath + "/" + category.getLargeImageFolder() + "/" + fileName);
        images.put("original", basePath + "/" + category.getOriginalImageFolder() + "/" + fileName);

        return new FactImageResponse(
                factId,
                images.get("large"),
                nullToEmpty(category.getAuthorName()),
                nullToEmpty(category.getAuthorUrl()),
                images
        );
    }

    private static void validateFactId(FactImageCategoryEntity category, Long factId) {
        if (factId == null) {
            throw new IllegalArgumentException("factId must not be null");
        }

        if (category == null) {
            throw new IllegalArgumentException("image category must not be null");
        }

        if (factId < category.getMinId() || factId > category.getMaxId()) {
            throw new IllegalArgumentException("factId is outside category range");
        }
    }

    private static String removeTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
