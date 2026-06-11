package com.gvapps.quotesfacts.dto;

import java.util.List;

public record ImageCollectionDTO(
        String title,
        String subTitle,
        List<FactImageResponse> images
) {
}
