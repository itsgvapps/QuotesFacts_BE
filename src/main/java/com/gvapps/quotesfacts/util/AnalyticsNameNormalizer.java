package com.gvapps.quotesfacts.util;

import org.springframework.stereotype.Component;

@Component
public class AnalyticsNameNormalizer {

    private static final String VALID_ANALYTICS_NAME_REGEX = "^[a-z][a-z0-9_]{0,79}$";

    public String normalize(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }

        String normalized = value.trim();

        normalized = normalized.replaceAll("([a-z0-9])([A-Z])", "$1_$2");
        normalized = normalized.replaceAll("[\\s\\-]+", "_");
        normalized = normalized.replaceAll("_+", "_");
        normalized = normalized.toLowerCase();

        if (normalized.startsWith("_")) {
            normalized = normalized.substring(1);
        }

        if (normalized.endsWith("_")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        return normalized;
    }

    public boolean isValidAnalyticsName(String value) {
        return value != null && value.matches(VALID_ANALYTICS_NAME_REGEX);
    }
}