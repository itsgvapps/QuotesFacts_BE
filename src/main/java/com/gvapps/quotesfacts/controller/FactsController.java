package com.gvapps.quotesfacts.controller;

import com.gvapps.quotesfacts.dto.FactDetailsDTO;
import com.gvapps.quotesfacts.dto.response.APIResponse;
import com.gvapps.quotesfacts.entity.FactDetailsEntity;
import com.gvapps.quotesfacts.entity.FactTypeEntity;
import com.gvapps.quotesfacts.service.FactTypeService;
import com.gvapps.quotesfacts.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/facts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // ✅ Allow access from mobile app or frontend
public class FactsController {

    private final FactTypeService factTypeService;

    // ✅ Get categories by type_id
    @GetMapping("/type/{typeId}")
    public ResponseEntity<APIResponse> getCategoriesByTypeId(@PathVariable int typeId) {
        List<FactTypeEntity> result = factTypeService.getCategoriesByTypeId(typeId);
        return ResponseEntity.ok(ResponseUtils.success("200", "Categories fetched successfully", result));
    }

    // ✅ Get tab data (Home / Discover)
    @GetMapping("/tab/{name}")
    public ResponseEntity<APIResponse> getTabData(@PathVariable String name) {
        Map<String, Object> tabData;
        switch (name.toLowerCase()) {
            case "home" -> tabData = factTypeService.getHomeTabData();
            case "discover" -> tabData = factTypeService.getDiscoverTabData();
            default -> throw new IllegalArgumentException("Invalid tab name: " + name);
        }
        return ResponseEntity.ok(ResponseUtils.success("200", name + " tab data fetched successfully", tabData));
    }

    // ✅ Get top popular categories
    @GetMapping("/popular")
    public ResponseEntity<APIResponse> getPopularCategories(@RequestParam(defaultValue = "4") int limit) {
        List<FactTypeEntity> result = factTypeService.getTopPopularCategories(limit);
        return ResponseEntity.ok(ResponseUtils.success("200", "Popular categories fetched successfully", result));
    }

    // ✅ Get top trending categories
    @GetMapping("/trending")
    public ResponseEntity<APIResponse> getTrendingCategories(@RequestParam(defaultValue = "4") int limit) {
        List<FactTypeEntity> result = factTypeService.getTopTrendingCategories(limit);
        return ResponseEntity.ok(ResponseUtils.success("200", "Trending categories fetched successfully", result));
    }

    // ✅ Create new fact
    @PostMapping
    public ResponseEntity<APIResponse> createFact(@RequestBody FactDetailsEntity fact) {
        FactDetailsEntity savedFact = factTypeService.saveFact(fact);
        return ResponseEntity.ok(ResponseUtils.success("200", "Fact created successfully", savedFact));
    }

    // ✅ Get fact by ID
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse> getFactById(@PathVariable int id) {
        return factTypeService.getFactById(id)
                .map(fact -> ResponseEntity.ok(ResponseUtils.success("200", "Fact fetched successfully", fact)))
                .orElse(ResponseEntity.ok(ResponseUtils.error("404", "Fact Not Found", "Fact not found with id: " + id)));
    }

    // ✅ Get facts by category
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<APIResponse> getFactsByCategory(@PathVariable int categoryId) {
        log.info("[FactController] >> [getFactsByCategory] categoryId: {}", categoryId);
        List<FactDetailsDTO> facts = factTypeService.getFactsByCategory(categoryId);
        return ResponseEntity.ok(ResponseUtils.success("200", "Facts fetched successfully", facts));
    }

    // ✅ Update category views
    @PostMapping("/category/views")
    public ResponseEntity<APIResponse> updateCategoryViews(@RequestBody Map<String, List<Long>> payload) {
        log.info("[FactController] >> [updateCategoryViews]");
        factTypeService.incrementCategoryViewsAsync(payload);
        return ResponseEntity.ok(ResponseUtils.success("200", "Category views updated successfully", null));
    }

    // ✅ Get facts by language
    @GetMapping("/language/{lang}")
    public ResponseEntity<APIResponse> getFactsByLanguage(@PathVariable("lang") String language) {
        List<FactDetailsEntity> result = factTypeService.getFactsByLanguage(language);
        return ResponseEntity.ok(ResponseUtils.success("200", "Facts fetched successfully", result));
    }

    // ✅ Get verified facts
    @GetMapping("/verified")
    public ResponseEntity<APIResponse> getVerifiedFacts() {
        List<FactDetailsEntity> result = factTypeService.getVerifiedFacts();
        return ResponseEntity.ok(ResponseUtils.success("200", "Verified facts fetched successfully", result));
    }

    // ✅ Get top viewed facts
    @GetMapping("/top")
    public ResponseEntity<APIResponse> getTopFacts(@RequestParam(defaultValue = "10") int limit) {
        List<FactDetailsEntity> result = factTypeService.getTopViewedFacts(limit);
        return ResponseEntity.ok(ResponseUtils.success("200", "Top viewed facts fetched successfully", result));
    }

    // ✅ Search facts by keyword
    @GetMapping("/search")
    public ResponseEntity<APIResponse> searchFacts(@RequestParam String q) {
        List<FactDetailsEntity> result = factTypeService.searchFactsByKeyword(q);
        return ResponseEntity.ok(ResponseUtils.success("200", "Facts fetched successfully", result));
    }

    // ✅ Update fact detail counts
    @PostMapping("/detail/counts")
    public ResponseEntity<APIResponse> updateFactDetailCounts(@RequestBody Map<String, List<Long>> payload) {
        log.info("[FactController] >> [updateFactDetailCounts]");
        factTypeService.incrementDetailCounts(payload);
        return ResponseEntity.ok(ResponseUtils.success("200", "Fact detail counts updated successfully", null));
    }
}
