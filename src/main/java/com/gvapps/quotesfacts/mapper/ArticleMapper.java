package com.gvapps.quotesfacts.mapper;

import com.gvapps.quotesfacts.dto.ArticleDetailDTO;
import com.gvapps.quotesfacts.dto.ArticleListDTO;
import com.gvapps.quotesfacts.dto.ArticleListProjection;
import com.gvapps.quotesfacts.entity.ArticlesEntity;

public class ArticleMapper {

    public static ArticleListDTO toDTO(ArticleListProjection p) {
        if (p == null) return null;

        return new ArticleListDTO(
                p.getId(),
                p.getTitle(),
                p.getSubTitle(),
                p.getSource(),
                p.getSummary(),
                p.getAuthor(),
                p.getActive(),
                p.getImgCredit(),
                p.getImgPath(),
                p.getExternalUrl()
        );
    }

    public static ArticleDetailDTO toDetailDTO(ArticlesEntity e) {
        if (e == null) return null;

        return new ArticleDetailDTO(
                e.getId(),
                e.getTitle(),
                e.getSubTitle(),
                e.getContent(),
                e.getSource(),
                e.getSummary(),
                e.getAuthor(),
                e.getImgCredit(),
                e.getImgPath(),
                e.getExternalUrl(),
                e.isActive(),
                e.getLikes(),
                e.getViews(),
                e.getDownloads(),
                e.getFavourites(),
                e.getBookmarks(),
                e.getReadingTime()
        );
    }

}
