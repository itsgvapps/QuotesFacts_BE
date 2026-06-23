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

    @Query(value = """
            SELECT *
            FROM content_image_set
            WHERE active = 1
              AND type_id = :typeId
              AND category_id = :categoryId
            ORDER BY RAND()
            LIMIT 1
            """, nativeQuery = true)
    Optional<ContentImageSetEntity> findRandomActiveByTypeIdAndCategoryId(
            @Param("typeId") int typeId,
            @Param("categoryId") int categoryId
    );

    Optional<ContentImageSetEntity> findByNameAndActiveTrue(String name);

    @Query(value = """
            SELECT *
            FROM content_image_set
            WHERE active = 1
              AND category_id = :categoryId
            ORDER BY RAND()
            """, nativeQuery = true)
    List<ContentImageSetEntity> findActiveByCategoryId(@Param("categoryId") int categoryId);
}
