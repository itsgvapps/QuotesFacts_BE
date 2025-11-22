package com.gvapps.quotesfacts.controller;

import com.gvapps.quotesfacts.dto.ArticleListDTO;
import com.gvapps.quotesfacts.dto.response.APIResponse;
import com.gvapps.quotesfacts.entity.ArticlesEntity;
import com.gvapps.quotesfacts.service.ArticlesService;
import com.gvapps.quotesfacts.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/articles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ArticlesController {

    private final ArticlesService articlesService;

    @GetMapping("/all")
    public ResponseEntity<APIResponse> getAll() {
        List<ArticlesEntity> articles = articlesService.getAll();
        return ResponseEntity.ok(ResponseUtils.success("200", "Fetched all articles", articles));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse> getById(@PathVariable Long id) {
        return articlesService.getById(id)
                .map(article -> ResponseEntity.ok(ResponseUtils.success("200", "Article found", article)))
                .orElse(ResponseEntity.ok(ResponseUtils.error("404", "Article not found", "No article with ID: " + id)));
    }

    @GetMapping("/byTag")
    public ResponseEntity<APIResponse> getArticlesByTag(
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<ArticleListDTO> articles = articlesService.getArticlesByTag(tag, page, size);
        return ResponseEntity.ok(ResponseUtils.success("200", "Fetched articles by tag", articles.getContent()));
    }

    @GetMapping("/search")
    public ResponseEntity<APIResponse> search(@RequestParam String keyword) {
        List<ArticlesEntity> articles = articlesService.search(keyword);
        return ResponseEntity.ok(ResponseUtils.success("200", "Articles search results", articles));
    }

    @GetMapping("/by-date")
    public ResponseEntity<APIResponse> getByDate(@RequestParam String date) {
        List<ArticlesEntity> articles = articlesService.findByDate(LocalDate.parse(date));
        return ResponseEntity.ok(ResponseUtils.success("200", "Articles fetched by date", articles));
    }

    @GetMapping("/top")
    public ResponseEntity<APIResponse> getTopArticles() {
        List<ArticlesEntity> articles = articlesService.findTopArticles();
        return ResponseEntity.ok(ResponseUtils.success("200", "Top articles fetched", articles));
    }

    @GetMapping("/tag1")
    public ResponseEntity<APIResponse> getByTag(@RequestParam String tag) {
        List<ArticlesEntity> articles = articlesService.findByTag(tag);
        return ResponseEntity.ok(ResponseUtils.success("200", "Articles fetched by tag", articles));
    }

    @PostMapping("/counts")
    public ResponseEntity<APIResponse> updateArticleCounts(@RequestBody Map<String, List<Long>> payload) {
        articlesService.incrementArticleCounts(payload);
        return ResponseEntity.ok(ResponseUtils.success("200", "Article counts updated successfully", null));
    }
}
