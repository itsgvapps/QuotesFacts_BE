package com.gvapps.quotesfacts.service;

import com.gvapps.quotesfacts.entity.FactDetailsEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FactDetailsService {

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
