package com.park.controller;

import com.park.common.PageResult;
import com.park.common.Result;
import com.park.entity.CmsArticle;
import com.park.mapper.CmsArticleMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class PublicArticleController {

    private final CmsArticleMapper articleMapper;

    @GetMapping
    public Result<PageResult<CmsArticle>> listArticles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {

        LambdaQueryWrapper<CmsArticle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsArticle::getStatus, 1);
        if (type != null && !type.isBlank()) {
            wrapper.eq(CmsArticle::getType, type);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(CmsArticle::getTitle, keyword);
        }
        wrapper.orderByDesc(CmsArticle::getPublishTime);

        Page<CmsArticle> result = articleMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.ok(new PageResult<>(result.getRecords(), result.getTotal(), page, size));
    }

    @GetMapping("/{id}")
    public Result<CmsArticle> getArticle(@PathVariable Long id) {
        CmsArticle article = articleMapper.selectOne(
                new LambdaQueryWrapper<CmsArticle>()
                        .eq(CmsArticle::getId, id)
                        .eq(CmsArticle::getStatus, 1));
        if (article == null) {
            return Result.fail(404, "文章不存在");
        }
        return Result.ok(article);
    }
}
