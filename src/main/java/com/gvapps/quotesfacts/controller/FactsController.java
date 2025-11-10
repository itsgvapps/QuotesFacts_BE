package com.gvapps.quotesfacts.controller;

import com.gvapps.quotesfacts.entity.FactDetailsEntity;
import com.gvapps.quotesfacts.entity.FactTypeEntity;
import com.gvapps.quotesfacts.service.FactDetailsService;
import com.gvapps.quotesfacts.service.FactTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/facts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // ✅ Allow access from mobile app or frontend
public class FactsController {

    private final FactDetailsService factDetailsService;

    private final FactTypeService factTypeService;

    // ✅ Get categories by type_id
    @GetMapping("/type/{typeId}")
    public ResponseEntity<List<FactTypeEntity>> getCategoriesByTypeId(@PathVariable int typeId) {
        return ResponseEntity.ok(factTypeService.getCategoriesByTypeId(typeId));
    }

    // ✅ Get tab data
    @GetMapping("/tab/{name}")
    public ResponseEntity<Map<String, Object>> getTabData(@PathVariable String name) {
        if (name.equals("Home"))
            return ResponseEntity.ok(factTypeService.getHomeTabData());
        if (name.equals("Discover"))
            return ResponseEntity.ok(factTypeService.getDiscoverTabData());
        return null;
    }

    // ✅ Get top popular categories (configurable)
    @GetMapping("/popular")
    public ResponseEntity<List<FactTypeEntity>> getPopularCategories(
            @RequestParam(defaultValue = "4") int limit) {
        return ResponseEntity.ok(factTypeService.getTopPopularCategories(limit));
    }

    // ✅ Get top trending categories (configurable)
    @GetMapping("/trending")
    public ResponseEntity<List<FactTypeEntity>> getTrendingCategories(
            @RequestParam(defaultValue = "4") int limit) {
        return ResponseEntity.ok(factTypeService.getTopTrendingCategories(limit));
    }

    //Fact details

    // ✅ Create new fact
    @PostMapping
    public ResponseEntity<FactDetailsEntity> createFact(@RequestBody FactDetailsEntity fact) {
        FactDetailsEntity savedFact = factDetailsService.saveFact(fact);
        return ResponseEntity.ok(savedFact);
    }


    // ✅ Get fact by ID
    @GetMapping("/{id}")
    public ResponseEntity<FactDetailsEntity> getFactById(@PathVariable int id) {
        return factDetailsService.getFactById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Get facts by category
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<FactDetailsEntity>> getFactsByCategory(@PathVariable int categoryId) {
        log.info("[FactController] >> [getFactsByCategory] categoryId:{}", categoryId);
        return ResponseEntity.ok(factDetailsService.getFactsByCategory(categoryId));
    }

    @PostMapping("/category/views")
    public ResponseEntity<String> updateCategoryViews(@RequestBody Map<String, List<Long>> payload) {
        log.info("[FactController] >> [updateFactDetailCounts]");
        factTypeService.incrementCategoryViewsAsync(payload);
        return ResponseEntity.ok("Category Counts updated successfully");
    }

    // ✅ Get facts by language
    @GetMapping("/language/{lang}")
    public ResponseEntity<List<FactDetailsEntity>> getFactsByLanguage(@PathVariable("lang") String language) {
        return ResponseEntity.ok(factDetailsService.getFactsByLanguage(language));
    }

    // ✅ Get verified facts only
    @GetMapping("/verified")
    public ResponseEntity<List<FactDetailsEntity>> getVerifiedFacts() {
        return ResponseEntity.ok(factDetailsService.getVerifiedFacts());
    }

    // ✅ Get top viewed facts
    @GetMapping("/top")
    public ResponseEntity<List<FactDetailsEntity>> getTopFacts(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(factDetailsService.getTopViewedFacts(limit));
    }

    // ✅ Search facts by keyword
    @GetMapping("/search")
    public ResponseEntity<List<FactDetailsEntity>> searchFacts(@RequestParam String q) {
        return ResponseEntity.ok(factDetailsService.searchFactsByKeyword(q));
    }

    @PostMapping("/detail/counts")
    public ResponseEntity<String> updateFactDetailCounts(@RequestBody Map<String, List<Long>> payload) {
        log.info("[FactController] >> [updateFactDetailCounts]");
        factDetailsService.incrementDetailCounts(payload);
        return ResponseEntity.ok("Counts updated successfully");
    }
}
