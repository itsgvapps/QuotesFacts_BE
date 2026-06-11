package com.gvapps.quotesfacts.service;

import com.gvapps.quotesfacts.dto.ImageCollectionDTO;

public interface ContentImageSetService {

    ImageCollectionDTO getRandomImages();

    ImageCollectionDTO getImagesBySetName(String setName);

    ImageCollectionDTO getImagesByCategoryId(int categoryId);

    ImageCollectionDTO getLatestImages();

    ImageCollectionDTO getHomeImages();
}
