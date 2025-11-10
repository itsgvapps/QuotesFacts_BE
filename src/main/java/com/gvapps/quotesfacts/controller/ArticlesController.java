package com.gvapps.quotesfacts.controller;

import com.gvapps.quotesfacts.entity.ArticlesEntity;
import com.gvapps.quotesfacts.service.ArticlesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // ✅ Allow access from mobile app or frontend
public class ArticlesController {

    private final ArticlesService articlesService;

    @GetMapping("/all")
    public List<ArticlesEntity> getAll() {
        return articlesService.getAll();
    }

    /**
     * Get articles by tag with pagination.
     * If tag is empty or not provided, returns all active articles.
     *
     * Example:
     *   GET /api/articles/byTag?tag=Motivation&page=0&size=10
     *   GET /api/articles/byTag?page=0&size=5   → returns all
     */
    @GetMapping("/byTag")
    public Page<ArticlesEntity> getArticlesByTag(
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return articlesService.getArticlesByTag(tag, page, size);
    }

    @GetMapping("/{id}")
    public Optional<ArticlesEntity> getById(@PathVariable Long id) {
        return articlesService.getById(id);
    }

    @GetMapping("/search")
    public List<ArticlesEntity> search(@RequestParam String keyword) {
        return articlesService.search(keyword);
    }

    @GetMapping("/by-date")
    public List<ArticlesEntity> getByDate(@RequestParam String date) {
        return articlesService.findByDate(LocalDate.parse(date));
    }

    @GetMapping("/top")
    public List<ArticlesEntity> getTopArticles() {
        return articlesService.findTopArticles();
    }

    @GetMapping("/tag1")
    public List<ArticlesEntity> getByTag(@RequestParam String tag) {
        return articlesService.findByTag(tag);
    }

    /**
     * POST /api/articles/counts
     * Example payload:
     * {
     *   "views": [1, 2],
     *   "likes": [1],
     *   "bookmarks": [3, 4]
     * }
     */
    @PostMapping("/counts")
    public ResponseEntity<String> updateArticleCounts(@RequestBody Map<String, List<Long>> payload) {
        articlesService.incrementArticleCounts(payload);
        return ResponseEntity.ok("Article counts updated successfully");
    }
}
