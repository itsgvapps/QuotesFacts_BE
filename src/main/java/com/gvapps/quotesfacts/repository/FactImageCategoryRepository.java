package com.gvapps.quotesfacts.repository;

import com.gvapps.quotesfacts.dto.FactImageCategoryProjection;
import com.gvapps.quotesfacts.entity.FactImageCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FactImageCategoryRepository extends JpaRepository<FactImageCategoryEntity, Long> {

    @Query(value = """
            SELECT
                image_category_id AS imageCategoryId,
                name,
                short_description AS shortDescription,
                square_image AS squareImage,
                vertical_image AS verticalImage,
                vertical_image_with_text AS verticalImageWithText,
                horizontal_image AS horizontalImage,
                horizontal_image_with_text AS horizontalImageWithText
             FROM fact_image_category
            WHERE active = 1
              AND type_id = :typeId
            ORDER BY RAND()
            LIMIT :limit
            """, nativeQuery = true)
    List<FactImageCategoryProjection> findByTypeIdAndActiveTrue(
            @Param("typeId") int typeId,
            @Param("limit") int limit
    );

    Optional<FactImageCategoryEntity> findByImageCategoryIdAndActiveTrue(Long imageCategoryId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
            UPDATE fact_image_category
            SET views = views + 1
            WHERE image_category_id = :imageCategoryId
            """, nativeQuery = true)
    int incrementViewsByImageCategoryId(@Param("imageCategoryId") Long imageCategoryId);
}

