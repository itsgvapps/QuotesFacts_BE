package com.gvapps.quotesfacts.service;

import com.gvapps.quotesfacts.entity.FactTypeEntity;
import java.util.List;
import java.util.Map;

public interface FactTypeService {

    List<FactTypeEntity> getAllCategories();

    List<FactTypeEntity> getCategoriesByTypeId(int typeId);


    List<FactTypeEntity> getTopPopularCategories(int limit);

    List<FactTypeEntity> getTopTrendingCategories(int limit);

    void incrementCategoryViewsAsync(Map<String, List<Long>> categoryIds);

    Map<String ,Object> getHomeTabData();

    Map<String, Object> getDiscoverTabData();

}
