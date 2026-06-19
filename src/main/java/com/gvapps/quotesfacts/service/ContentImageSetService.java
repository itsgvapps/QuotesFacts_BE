package com.gvapps.quotesfacts.service;

import com.gvapps.quotesfacts.dto.ImageCollectionDTO;

public interface ContentImageSetService {

    ImageCollectionDTO getRandomImages(int typeId, int categoryId);

    ImageCollectionDTO getImagesBySetName(String setName);

    ImageCollectionDTO getImagesByCategoryId(int categoryId);

    ImageCollectionDTO getLatestImages(int typeId, int categoryId);

    ImageCollectionDTO getHomeImages(int typeId, int categoryId);
}
