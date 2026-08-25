package com.gvapps.quotesfacts.repository;

import com.gvapps.quotesfacts.entity.ContentImageSetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentImageSetRepository extends JpaRepository<ContentImageSetEntity, Long> {

    /**
     * Returns one random active image set for the requested type/category.
     * <p>
     * LIMIT 1 keeps the database result and Hibernate allocation bounded.
     */
    @Query(value = """
            SELECT *
            FROM content_image_set
            WHERE active = 1
              AND type_id = :typeId
              AND category_id = :categoryId
            ORDER BY RAND()
            LIMIT 1
            """, nativeQuery = true)
    Optional<ContentImageSetEntity> findRandomActiveByTypeIdAndCategoryId(@Param("typeId") int typeId, @Param("categoryId") int categoryId);

    /**
     * Returns one active image set by its name.
     */
    Optional<ContentImageSetEntity> findByNameAndActiveTrue(String name);

    /**
     * Returns active image sets for a category.
     * <p>
     * IMPORTANT:
     * The previous implementation returned every active row.
     * <p>
     * The service only needs a bounded number of image sets to generate
     * the requested images, so keep the DB result bounded.
     * <p>
     * Adjust the LIMIT if your business logic requires more image sets.
     */
    @Query(value = """
            SELECT *
            FROM content_image_set
            WHERE active = 1
              AND category_id = :categoryId
            ORDER BY RAND()
            LIMIT :limit
            """, nativeQuery = true)
    List<ContentImageSetEntity> findActiveByCategoryId(@Param("categoryId") int categoryId, @Param("limit") int limit);
}