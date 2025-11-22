package com.gvapps.quotesfacts.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ArticleDetailDTO(
        Long id,
        String title,
        String subTitle,
        String content,
        String source,
        String summary,
        String author,
        String imgCredit,
        String imgPath,
        String externalUrl,
        boolean active,
        int likes,
        int views,
        int downloads,
        int favourites,
        int bookmarks,
        int readingTime
) {
}
