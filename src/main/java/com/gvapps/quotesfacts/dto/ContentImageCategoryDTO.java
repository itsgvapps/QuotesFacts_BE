package com.gvapps.quotesfacts.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Deprecated
public record ContentImageCategoryDTO(
        Long imageCategoryId,
        String name,
        @JsonProperty("short_description")
        String shortDescription,
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
