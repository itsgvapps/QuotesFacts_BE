package com.gvapps.quotesfacts.repository;

import com.gvapps.quotesfacts.entity.FactDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FactDetailsRepository extends JpaRepository<FactDetailsEntity, Integer> {

    // ✅ Find facts by category
    List<FactDetailsEntity> findByCategoryId(int categoryId);

    @Query(value = """
    SELECT * FROM FACT_DETAILS
    WHERE category_id = :categoryId
    ORDER BY RAND()
    LIMIT :limit
    """, nativeQuery = true)
    List<FactDetailsEntity> findRandomByCategoryId(
            @Param("categoryId") int categoryId,
            @Param("limit") int limit
    );

    // ✅ Find facts by language
    List<FactDetailsEntity> findByLanguage(String language);

    // ✅ Find only verified facts
    List<FactDetailsEntity> findByVerifiedTrue();

    // ✅ Find featured / most viewed facts (custom sorting via query methods)
    List<FactDetailsEntity> findTop10ByOrderByViewsDesc();

    // ✅ Optional: search by text content (case-insensitive)
    List<FactDetailsEntity> findByTextContainingIgnoreCase(String keyword);

    @Transactional
    @Modifying
    @Query("UPDATE FactDetailsEntity f SET f.views = f.views + 1 WHERE f.id IN :ids")
    void incrementViews(List<Long> ids);

    @Transactional
    @Modifying
    @Query("UPDATE FactDetailsEntity f SET f.likes = f.likes + 1 WHERE f.id IN :ids")
    void incrementLikes(List<Long> ids);

    @Transactional
    @Modifying
    @Query("UPDATE FactDetailsEntity f SET f.bookmarks = f.bookmarks + 1 WHERE f.id IN :ids")
    void incrementBookmarks(List<Long> ids);

    @Transactional
    @Modifying
    @Query("UPDATE FactDetailsEntity f SET f.downloads = f.downloads + 1 WHERE f.id IN :ids")
    void incrementDownloads(List<Long> ids);

    @Transactional
    @Modifying
    @Query("UPDATE FactDetailsEntity f SET f.shares = f.shares + 1 WHERE f.id IN :ids")
    void incrementShares(List<Long> ids);

    @Query(value = """
        SELECT * FROM FACT_DETAILS
        WHERE LENGTH(text) < 160
        ORDER BY RAND()
        LIMIT 5
        """, nativeQuery = true)
    List<FactDetailsEntity> findRandomShortFacts();
}
