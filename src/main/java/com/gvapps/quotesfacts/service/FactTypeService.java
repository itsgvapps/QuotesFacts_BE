package com.gvapps.quotesfacts.service;

import com.gvapps.quotesfacts.entity.FactDetailsEntity;
import com.gvapps.quotesfacts.entity.FactTypeEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FactTypeService {

    List<FactTypeEntity> getAllCategories();

    List<FactTypeEntity> getCategoriesByTypeId(int typeId);


    List<FactTypeEntity> getTopPopularCategories(int limit);

    List<FactTypeEntity> getTopTrendingCategories(int limit);

    void incrementCategoryViewsAsync(Map<String, List<Long>> categoryIds);

    Map<String ,Object> getHomeTabData();

    Map<String, Object> getDiscoverTabData();

    //Fact details

    FactDetailsEntity saveFact(FactDetailsEntity fact);

    Optional<FactDetailsEntity> getFactById(int id);

    List<FactDetailsEntity> getAllFacts();

    List<FactDetailsEntity> getFactsByCategory(int categoryId);

    List<FactDetailsEntity> getFactsByLanguage(String language);

    List<FactDetailsEntity> getVerifiedFacts();

    List<FactDetailsEntity> getTopViewedFacts(int limit);

    List<FactDetailsEntity> searchFactsByKeyword(String keyword);

    FactDetailsEntity updateFact(int id, FactDetailsEntity updatedFact);

    void deleteFact(int id);

    void incrementDetailCounts(Map<String, List<Long>> payload);

}
