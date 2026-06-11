package com.gvapps.quotesfacts.dto;

@Deprecated
public interface ContentImageCategoryProjection {
    Long getImageCategoryId();

    String getName();

    String getTitle();

    String getSubTitle();

    String getVerticalImage();

    String getVerticalImageWithText();

    String getHorizontalImage();

    String getHorizontalImageWithText();
}
