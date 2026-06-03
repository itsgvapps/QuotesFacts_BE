package com.gvapps.quotesfacts.repository;

import org.springframework.stereotype.Repository;

/**
 * Backward-compatibility alias. The repository was renamed to {@link FactImageCategoryRepository}.
 * Keep this interface so older code importing the old name continues to work until callers are migrated.
 */
@Repository
public interface ContentImageCategoryRepository extends FactImageCategoryRepository {

}
