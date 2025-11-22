package com.gvapps.quotesfacts.dto;

public interface FactDetailsProjection {
    int getId();

    int getCategoryId();

    String getText();

    String getShortSummary();

    String getLongSummary();

    int getArticleId();

    String getSourceUrl();

    int getLikes();

    int getBookmarks();

    int getDownloads();

    int getShares();

    int getViews();

    boolean isVerified();
}
