package com.gvapps.quotesfacts.controller;

import com.gvapps.quotesfacts.dto.FactDetailsDTO;
import com.gvapps.quotesfacts.dto.ImageCollectionDTO;
import com.gvapps.quotesfacts.dto.response.APIResponse;
import com.gvapps.quotesfacts.entity.FactTypeEntity;
import com.gvapps.quotesfacts.service.ContentImageSetService;
import com.gvapps.quotesfacts.service.PsychologyTypeService;
import com.gvapps.quotesfacts.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.gvapps.quotesfacts.util.Constants.SET_IMAGE_TYPE_PSYCHOLOGY;

@Slf4j
@RestController
@RequestMapping("/v1/psychology")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // ✅ Allow access from mobile app or frontend
public class PsychologyController {
    private final PsychologyTypeService psychologyTypeService;
    private final ContentImageSetService contentImageSetService;

    // Get
    @GetMapping("/text-categories")
    public ResponseEntity<APIResponse> getTextCategoriesByTypeId(@RequestParam int typeId) {
        List<FactTypeEntity> result = psychologyTypeService.getCategoriesByTypeId(typeId);
        return ResponseEntity.ok(ResponseUtils.success("200", "Categories fetched successfully", result));
    }

    // ✅ GET category views
    @GetMapping("/text-categories/{textCategoryId}/texts")
    public ResponseEntity<APIResponse> getTextsByTextCategoryId(@PathVariable int textCategoryId) {
        List<FactDetailsDTO> facts = psychologyTypeService.getFactsByCategory(textCategoryId);
        if (facts != null)
            log.info("[FactController] >> [getTextsByTextCategoryId] [response] >> textCategoryId:{}; total items: {}", textCategoryId, facts.size());
        return ResponseEntity.ok(ResponseUtils.success("200", "Facts fetched successfully", facts));
    }

    // ✅ Get fact images by image category ID
    @GetMapping("/image-categories/{imageCategoryId}/images")
    public ResponseEntity<APIResponse> getFactImagesByImageCategoryId(@PathVariable Long imageCategoryId) {
        ImageCollectionDTO result = psychologyTypeService.getFactImagesByImageCategoryId(imageCategoryId);
        return ResponseEntity.ok(ResponseUtils.success("200", "Fact images fetched successfully", result.images(), result.title(), result.subTitle()));
    }

    // ✅ Update fact detail counts (views, shares, likes, etc.)
    @PostMapping("/detail/counts")
    public ResponseEntity<APIResponse> updateFactDetailCounts(@RequestBody Map<String, List<Long>> payload) {
        psychologyTypeService.incrementDetailCounts(payload);
        return ResponseEntity.ok(ResponseUtils.success("200", "Fact detail counts updated successfully", null));
    }

    @PostMapping("/category/views")
    public ResponseEntity<APIResponse> updateCategoryViews(@RequestBody Map<String, List<Long>> payload) {
        psychologyTypeService.incrementCategoryViewsAsync(payload);
        return ResponseEntity.ok(ResponseUtils.success("200", "Category views updated successfully", null));
    }

    /* get Images */
    @GetMapping("/images/latest")
    public ResponseEntity<APIResponse> getLatestImages() {
        ImageCollectionDTO result = contentImageSetService.getLatestImages(SET_IMAGE_TYPE_PSYCHOLOGY, 1);
        return ResponseEntity.ok(ResponseUtils.success("200", "Images fetched successfully", result.images(), result.title(), result.subTitle()));
    }

    /* get Images */
    @GetMapping("/images/random")
    public ResponseEntity<APIResponse> getRandomImages() {
        ImageCollectionDTO result = contentImageSetService.getRandomImages(SET_IMAGE_TYPE_PSYCHOLOGY, 1);
        return ResponseEntity.ok(ResponseUtils.success("200", "Images fetched successfully", result.images(), result.title(), result.subTitle()));
    }

    // ✅ Get tab data (home, discover, etc.)
    @GetMapping("/tab/{name}")
    public ResponseEntity<APIResponse> getTabData(@PathVariable String name) {
        Map<String, Object> tabData;
        switch (name.toLowerCase()) {
            case "home" -> tabData = psychologyTypeService.getHomeTabData();
            case "discover" -> tabData = psychologyTypeService.getDiscoverTabData();
            default -> throw new IllegalArgumentException("Invalid tab name: " + name);
        }
        return ResponseEntity.ok(ResponseUtils.success("200", name + " tab data fetched successfully", tabData));
    }
}
