package com.gvapps.quotesfacts.util;

import com.gvapps.quotesfacts.dto.FactImageResponse;
import com.gvapps.quotesfacts.entity.ContentImageSetEntity;

import java.util.LinkedHashMap;
import java.util.Map;

public class ContentImageSetBuilder {

    private ContentImageSetBuilder() {
    }

    public static FactImageResponse build(ContentImageSetEntity imageSet, Long imageId) {
        validateImageId(imageSet, imageId);

        long serialNumber = imageId - imageSet.getMinId() + 1;
        String fileName = nullToEmpty(imageSet.getImagePrefix()) + serialNumber + "." + nullToDefault(imageSet.getImageExtension(), "webp");
        String basePath = removeTrailingSlash(imageSet.getBaseFolder());

        Map<String, String> images = new LinkedHashMap<>();
        images.put("small", buildPath(basePath, nullToDefault(imageSet.getSmallImageFolder(), "small"), fileName));
        images.put("medium", buildPath(basePath, nullToDefault(imageSet.getMediumImageFolder(), "medium"), fileName));
        images.put("large", buildPath(basePath, nullToDefault(imageSet.getLargeImageFolder(), "large"), fileName));
        images.put("original", buildPath(basePath, nullToDefault(imageSet.getOriginalImageFolder(), "original"), fileName));

        return new FactImageResponse(
                imageId,
                images.get("large"),
                nullToEmpty(imageSet.getAuthorName()),
                nullToEmpty(imageSet.getAuthorUrl()),
                images
        );
    }

    private static void validateImageId(ContentImageSetEntity imageSet, Long imageId) {
        if (imageSet == null) {
            throw new IllegalArgumentException("imageSet must not be null");
        }
        if (imageId == null) {
            throw new IllegalArgumentException("imageId must not be null");
        }
        if (imageSet.getMinId() == null || imageSet.getMaxId() == null || imageSet.getMaxId() < imageSet.getMinId()) {
            throw new IllegalArgumentException("imageSet has invalid id range");
        }
        if (imageId < imageSet.getMinId() || imageId > imageSet.getMaxId()) {
            throw new IllegalArgumentException("imageId is outside imageSet range");
        }
    }

    private static String buildPath(String basePath, String folder, String fileName) {
        if (basePath.isBlank()) {
            return folder + "/" + fileName;
        }
        return basePath + "/" + folder + "/" + fileName;
    }

    private static String removeTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private static String nullToDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
