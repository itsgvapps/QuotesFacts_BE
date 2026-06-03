package com.gvapps.quotesfacts.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FactImageCategoryDTO(
        Long imageCategoryId,
        String name,
        @JsonProperty("short_description")
        String shortDescription,
        @JsonProperty("square_image")
        String squareImage,
        @JsonProperty("vertical_image")
        String verticalImage,
        @JsonProperty("vertical_image_with_text")
        String verticalImageWithText,
        @JsonProperty("horizontal_image")
        String horizontalImage,
        @JsonProperty("horizontal_image_with_text")
        String horizontalImageWithText
) {
}

