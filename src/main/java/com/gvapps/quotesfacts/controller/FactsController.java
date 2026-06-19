package com.gvapps.quotesfacts.controller;

import com.gvapps.quotesfacts.dto.FactDetailsDTO;
import com.gvapps.quotesfacts.dto.FactImageCategoryDTO;
import com.gvapps.quotesfacts.dto.ImageCollectionDTO;
import com.gvapps.quotesfacts.dto.response.APIResponse;
import com.gvapps.quotesfacts.entity.FactDetailsEntity;
import com.gvapps.quotesfacts.entity.FactTypeEntity;
import com.gvapps.quotesfacts.service.ContentImageSetService;
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
    private final ContentImageSetService contentImageSetService;

    // ✅ Get categories by type_id
    @GetMapping("/type/{typeId}")
    public ResponseEntity<APIResponse> getCategoriesByTypeId(@PathVariable int typeId) {
        List<FactTypeEntity> result = factTypeService.getCategoriesByTypeId(typeId);
        return ResponseEntity.ok(ResponseUtils.success("200", "Categories fetched successfully", result));
    }

    // Get
    @GetMapping("/text-categories")
    public ResponseEntity<APIResponse> getTextCategoriesByTypeId(@RequestParam int typeId) {
        List<FactTypeEntity> result = factTypeService.getCategoriesByTypeId(typeId);
        return ResponseEntity.ok(ResponseUtils.success("200", "Categories fetched successfully", result));
    }

    // Get image categories by type_id. Default limit is 4 for home/discover sections.
    @GetMapping("/image-categories")
    public ResponseEntity<APIResponse> getImageCategoriesByTypeId(@RequestParam int typeId) {
        List<FactImageCategoryDTO> result = factTypeService.getImageCategoriesByTypeId(typeId);
        return ResponseEntity.ok(ResponseUtils.success("200", "Image categories fetched successfully", result));
    }

    // ✅ Get tab data (Home / Discover)
    @GetMapping("/image-categories/{imageCategoryId}/images")
    public ResponseEntity<APIResponse> getFactImagesByImageCategoryId(@PathVariable Long imageCategoryId) {
        ImageCollectionDTO result = factTypeService.getFactImagesByImageCategoryId(imageCategoryId);
        return ResponseEntity.ok(ResponseUtils.success("200", "Fact images fetched successfully", result.images(), result.title(), result.subTitle()));
    }

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
        List<FactDetailsDTO> facts = factTypeService.getFactsByCategory(categoryId);
        if (facts != null)
            log.info("[FactController] >> [getFactsByCategory] [response] >> categoryId:{}; total items: {}", categoryId, facts.size());
        return ResponseEntity.ok(ResponseUtils.success("200", "Facts fetched successfully", facts));
    }

    // ✅ Update category views
    @GetMapping("/text-categories/{textCategoryId}/texts")
    public ResponseEntity<APIResponse> getTextsByTextCategoryId(@PathVariable int textCategoryId) {
        List<FactDetailsDTO> facts = factTypeService.getFactsByCategory(textCategoryId);
        if (facts != null)
            log.info("[FactController] >> [getTextsByTextCategoryId] [response] >> textCategoryId:{}; total items: {}", textCategoryId, facts.size());
        return ResponseEntity.ok(ResponseUtils.success("200", "Facts fetched successfully", facts));
    }

    @PostMapping("/category/views")
    public ResponseEntity<APIResponse> updateCategoryViews(@RequestBody Map<String, List<Long>> payload) {
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
        factTypeService.incrementDetailCounts(payload);
        return ResponseEntity.ok(ResponseUtils.success("200", "Fact detail counts updated successfully", null));
    }

    @GetMapping("/images/sets/{setName}")
    public ResponseEntity<APIResponse> getImagesBySetName(@PathVariable String setName) {
        ImageCollectionDTO result = contentImageSetService.getImagesBySetName(setName);
        return ResponseEntity.ok(ResponseUtils.success("200", "Images fetched successfully", result.images(), result.title(), result.subTitle()));
    }

    @GetMapping("/images/categories/{categoryId}")
    public ResponseEntity<APIResponse> getImagesByCategoryId(@PathVariable int categoryId) {
        ImageCollectionDTO result = contentImageSetService.getImagesByCategoryId(categoryId);
        return ResponseEntity.ok(ResponseUtils.success("200", "Images fetched successfully", result.images(), result.title(), result.subTitle()));
    }

    @GetMapping("/images/latest")
    public ResponseEntity<APIResponse> getLatestImages() {
        ImageCollectionDTO result = contentImageSetService.getLatestImages(11, 1);
        return ResponseEntity.ok(ResponseUtils.success("200", "Images fetched successfully", result.images(), result.title(), result.subTitle()));
    }

    /* get Images */
    @GetMapping("/images/random")
    public ResponseEntity<APIResponse> getRandomImages() {
        ImageCollectionDTO result = contentImageSetService.getRandomImages(11, 1);
        return ResponseEntity.ok(ResponseUtils.success("200", "Images fetched successfully", result.images(), result.title(), result.subTitle()));
    }
}
