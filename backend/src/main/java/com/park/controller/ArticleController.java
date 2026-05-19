package com.park.controller;

import com.park.common.PageResult;
import com.park.common.Result;
import com.park.dto.ArticleQueryDTO;
import com.park.entity.CmsArticle;
import com.park.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public Result<PageResult<CmsArticle>> listArticles(@Valid ArticleQueryDTO query) {
        return Result.ok(articleService.listArticles(query));
    }

    @GetMapping("/{id}")
    public Result<CmsArticle> getArticle(@PathVariable Long id) {
        CmsArticle article = articleService.getArticleById(id);
        if (article == null) {
            return Result.fail(404, "文章不存在");
        }
        return Result.ok(article);
    }

    @PostMapping
    public Result<Void> createArticle(@Valid @RequestBody CmsArticle article) {
        article.setCreateBy(getCurrentUser());
        article.setUpdateBy(getCurrentUser());
        articleService.createArticle(article);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> updateArticle(@PathVariable Long id, @Valid @RequestBody CmsArticle article) {
        article.setId(id);
        article.setUpdateBy(getCurrentUser());
        articleService.updateArticle(article);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return Result.ok();
    }

    @PatchMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        articleService.updateStatus(id, status);
        return Result.ok();
    }

    private String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
