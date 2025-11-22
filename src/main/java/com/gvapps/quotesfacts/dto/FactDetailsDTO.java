package com.gvapps.quotesfacts.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)  // Do NOT send nulls
public record FactDetailsDTO(
        int id,
        int categoryId,
        String text,
        String shortSummary,
        String longSummary,
        int articleId,
        String sourceUrl,
        int likes,
        int bookmarks,
        int downloads,
        int shares,
        int views,
        boolean verified
) {
}
