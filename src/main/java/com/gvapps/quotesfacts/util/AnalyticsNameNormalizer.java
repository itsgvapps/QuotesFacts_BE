package com.gvapps.quotesfacts.util;

import org.springframework.stereotype.Component;

@Component
public class AnalyticsNameNormalizer {

    public String normalize(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }

        String normalized = value.trim();

        // Convert camelCase to snake_case
        normalized = normalized.replaceAll("([a-z0-9])([A-Z])", "$1_$2");

        // Convert spaces/hyphens to underscore
        normalized = normalized.replaceAll("[\\s\\-]+", "_");

        // Remove duplicate underscores
        normalized = normalized.replaceAll("_+", "_");

        return normalized.toLowerCase();
    }
}