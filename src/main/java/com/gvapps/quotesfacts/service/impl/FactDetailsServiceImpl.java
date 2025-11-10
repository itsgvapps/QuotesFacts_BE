package com.gvapps.quotesfacts.service.impl;

import com.gvapps.quotesfacts.entity.FactDetailsEntity;
import com.gvapps.quotesfacts.repository.FactDetailsRepository;
import com.gvapps.quotesfacts.service.FactDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FactDetailsServiceImpl implements FactDetailsService {

    private final FactDetailsRepository factDetailsRepository;

    @Override
    public FactDetailsEntity saveFact(FactDetailsEntity fact) {
        return factDetailsRepository.save(fact);
    }

    @Override
    public Optional<FactDetailsEntity> getFactById(int id) {
        return factDetailsRepository.findById(id);
    }

    @Override
    public List<FactDetailsEntity> getAllFacts() {
        return factDetailsRepository.findAll();
    }

    @Override
    public List<FactDetailsEntity> getFactsByCategory(int categoryId) {
        //return factDetailsRepository.findByCategoryId(categoryId);
        return factDetailsRepository.findRandomByCategoryId(categoryId, 100);

    }

    @Override
    public List<FactDetailsEntity> getFactsByLanguage(String language) {
        return factDetailsRepository.findByLanguage(language);
    }

    @Override
    public List<FactDetailsEntity> getVerifiedFacts() {
        return factDetailsRepository.findByVerifiedTrue();
    }

    @Override
    public List<FactDetailsEntity> getTopViewedFacts(int limit) {
        List<FactDetailsEntity> topFacts = factDetailsRepository.findTop10ByOrderByViewsDesc();
        return topFacts.size() > limit ? topFacts.subList(0, limit) : topFacts;
    }

    @Override
    public List<FactDetailsEntity> searchFactsByKeyword(String keyword) {
        return factDetailsRepository.findByTextContainingIgnoreCase(keyword);
    }

    @Override
    public FactDetailsEntity updateFact(int id, FactDetailsEntity updatedFact) {
        return factDetailsRepository.findById(id)
                .map(existingFact -> {
                    existingFact.setText(updatedFact.getText());
                    existingFact.setCategoryId(updatedFact.getCategoryId());
                    existingFact.setShortSummary(updatedFact.getShortSummary());
                    existingFact.setLongSummary(updatedFact.getLongSummary());
                    existingFact.setSourceUrl(updatedFact.getSourceUrl());
                    existingFact.setLikes(updatedFact.getLikes());
                    existingFact.setBookmarks(updatedFact.getBookmarks());
                    existingFact.setDownloads(updatedFact.getDownloads());
                    existingFact.setShares(updatedFact.getShares());
                    existingFact.setViews(updatedFact.getViews());
                    existingFact.setTags(updatedFact.getTags());
                    existingFact.setVerified(updatedFact.isVerified());
                    existingFact.setLanguage(updatedFact.getLanguage());
                    existingFact.setAddedDate(updatedFact.getAddedDate());
                    existingFact.setUpdatedDate(updatedFact.getUpdatedDate());
                    return factDetailsRepository.save(existingFact);
                })
                .orElseThrow(() -> new RuntimeException("Fact not found with id " + id));
    }

    @Override
    public void deleteFact(int id) {
        factDetailsRepository.deleteById(id);
    }

    @Transactional
    public void incrementDetailCounts(Map<String, List<Long>> payload) {

        if (payload.containsKey("views")) {
            List<Long> ids = payload.get("views");
            if (ids != null && !ids.isEmpty()) factDetailsRepository.incrementViews(ids);
        }

        if (payload.containsKey("likes")) {
            List<Long> ids = payload.get("likes");
            if (ids != null && !ids.isEmpty()) factDetailsRepository.incrementLikes(ids);
        }

        if (payload.containsKey("bookmarks")) {
            List<Long> ids = payload.get("bookmarks");
            if (ids != null && !ids.isEmpty()) factDetailsRepository.incrementBookmarks(ids);
        }

        if (payload.containsKey("downloads")) {
            List<Long> ids = payload.get("downloads");
            if (ids != null && !ids.isEmpty()) factDetailsRepository.incrementDownloads(ids);
        }

        if (payload.containsKey("shares")) {
            List<Long> ids = payload.get("shares");
            if (ids != null && !ids.isEmpty()) factDetailsRepository.incrementShares(ids);
        }
    }
}
