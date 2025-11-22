package com.gvapps.quotesfacts.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)   // Do not return null fields
public record ArticleListDTO(
        Long id,
        String title,
        String subTitle,
        String source,
        String summary,
        String author,
        Boolean active,
        String imgCredit,
        String imgPath,
        String externalUrl
) {
}
