package com.gvapps.quotesfacts.dto;

@Deprecated
public interface ContentImageCategoryProjection {
    Long getImageCategoryId();

    String getName();

    String getShortDescription();

    String getVerticalImage();

    String getVerticalImageWithText();

    String getHorizontalImage();

    String getHorizontalImageWithText();
}
