package com.gvapps.quotesfacts.repository;

import com.gvapps.quotesfacts.entity.ArticlesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ArticlesRepository extends JpaRepository<ArticlesEntity, Long> {

    List<ArticlesEntity> findByActiveTrue();

    List<ArticlesEntity> findByFeaturedTrue();

    List<ArticlesEntity> findByCategoryId(String categoryId);

    @Modifying
    @Query("UPDATE ArticlesEntity a SET a.views = a.views + 1 WHERE a.id IN :ids")
    void incrementViews(List<Long> ids);

    @Modifying
    @Query("UPDATE ArticlesEntity a SET a.likes = a.likes + 1 WHERE a.id IN :ids")
    void incrementLikes(List<Long> ids);

    @Modifying
    @Query("UPDATE ArticlesEntity a SET a.downloads = a.downloads + 1 WHERE a.id IN :ids")
    void incrementDownloads(List<Long> ids);

    @Modifying
    @Query("UPDATE ArticlesEntity a SET a.favourites = a.favourites + 1 WHERE a.id IN :ids")
    void incrementFavourites(List<Long> ids);

    @Modifying
    @Query("UPDATE ArticlesEntity a SET a.bookmarks = a.bookmarks + 1 WHERE a.id IN :ids")
    void incrementBookmarks(List<Long> ids);

    @Query("SELECT a FROM ArticlesEntity a WHERE a.addedDate = :date")
    List<ArticlesEntity> findByAddedDate(LocalDate date);

    @Query("SELECT a FROM ArticlesEntity a WHERE " +
            "LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.summary) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ArticlesEntity> searchByKeyword(String keyword);

    @Query(value = "SELECT * FROM articles WHERE JSON_CONTAINS(tags, :tag, '$') LIMIT 5", nativeQuery = true)
    List<ArticlesEntity> findRandomByTag(String tag);

    @Query(value = """
        SELECT * FROM articles 
            WHERE (:tag IS NULL OR :tag = '' 
                   OR JSON_SEARCH(LOWER(tags), 'one', LOWER(:tag), NULL, '$') IS NOT NULL)
        AND active = 1
        ORDER BY RAND()
        """,
            countQuery = """
        SELECT COUNT(*) FROM articles 
                    WHERE (:tag IS NULL OR :tag = '' 
                           OR JSON_SEARCH(LOWER(tags), 'one', LOWER(:tag), NULL, '$') IS NOT NULL)
        AND active = 1
        """,
            nativeQuery = true)
    Page<ArticlesEntity> findByTagOrAll(@Param("tag") String tag, Pageable pageable);


    @Query(value = """
        SELECT * FROM articles
        WHERE active = TRUE
        ORDER BY views DESC
        LIMIT 5
        """, nativeQuery = true)
    List<ArticlesEntity> findTop5Articles();

    @Query(value = "SELECT * FROM articles WHERE JSON_SEARCH(tags, 'one', :tag) IS NOT NULL AND active = true LIMIT :limit", nativeQuery = true)
    List<ArticlesEntity> findTopByTag(@Param("tag") String tag, @Param("limit") int limit);
}
