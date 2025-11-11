package com.gvapps.quotesfacts.repository;

import com.gvapps.quotesfacts.entity.FactTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FactTypeRepository extends JpaRepository<FactTypeEntity, Integer> {

    // Finds all FactTypeEntity records where typeId = ? and active = true
    List<FactTypeEntity> findByTypeIdAndActiveTrue(int typeId);

    @Transactional
    @Modifying
    @Query("UPDATE FactTypeEntity f SET f.views = f.views + 1 WHERE f.id IN :categoryIds")
    void incrementViewsByCategoryIds(List<Long> categoryIds);

    // ✅ Get top N popular categories (based on views)
    List<FactTypeEntity> findTop4ByOrderByViewsDesc();

    // ✅ Get top N trending categories (recently updated)
    List<FactTypeEntity> findTop4ByOrderByUpdatedDateDesc();

    @Query(value = """
            SELECT * FROM fact_type
        WHERE active = TRUE
        ORDER BY views DESC
        LIMIT 4
        """, nativeQuery = true)
    List<FactTypeEntity> findTop4PopularCategories();

    @Query(value = "SELECT * FROM fact_type WHERE type_id = :typeId AND active = true ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<FactTypeEntity> findTopByTypeIdAndActiveTrue(@Param("typeId") int typeId, @Param("limit") int limit);
}
