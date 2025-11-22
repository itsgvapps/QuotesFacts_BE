package com.gvapps.quotesfacts.dto;

public interface ArticleListProjection {
    Long getId();

    String getTitle();

    String getSubTitle();

    String getSource();

    String getSummary();

    String getAuthor();

    Boolean getActive();

    String getImgCredit();

    String getImgPath();

    String getExternalUrl();
}
