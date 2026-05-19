package com.park.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.common.PageResult;
import com.park.dto.ArticleQueryDTO;
import com.park.entity.CmsArticle;
import com.park.mapper.CmsArticleMapper;
import com.park.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final CmsArticleMapper articleMapper;

    @Override
    public PageResult<CmsArticle> listArticles(ArticleQueryDTO query) {
        LambdaQueryWrapper<CmsArticle> wrapper = new LambdaQueryWrapper<>();
        if (query.getTitle() != null && !query.getTitle().isBlank()) {
            wrapper.like(CmsArticle::getTitle, query.getTitle());
        }
        if (query.getType() != null && !query.getType().isBlank()) {
            wrapper.eq(CmsArticle::getType, query.getType());
        }
        if (query.getStatus() != null) {
            wrapper.eq(CmsArticle::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(CmsArticle::getSortOrder).orderByDesc(CmsArticle::getCreateTime);

        Page<CmsArticle> page = articleMapper.selectPage(
                new Page<>(query.getPage(), query.getSize()), wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal(), query.getPage(), query.getSize());
    }

    @Override
    public CmsArticle getArticleById(Long id) {
        return articleMapper.selectById(id);
    }

    @Override
    public void createArticle(CmsArticle article) {
        if (article.getStatus() != null && article.getStatus() == 1 && article.getPublishTime() == null) {
            article.setPublishTime(LocalDateTime.now());
        }
        articleMapper.insert(article);
    }

    @Override
    public void updateArticle(CmsArticle article) {
        CmsArticle existing = articleMapper.selectById(article.getId());
        if (existing != null && existing.getStatus() == 0 && article.getStatus() != null && article.getStatus() == 1) {
            article.setPublishTime(LocalDateTime.now());
        }
        articleMapper.updateById(article);
    }

    @Override
    public void deleteArticle(Long id) {
        articleMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        CmsArticle article = new CmsArticle();
        article.setId(id);
        article.setStatus(status);
        if (status == 1) {
            article.setPublishTime(LocalDateTime.now());
        }
        articleMapper.updateById(article);
    }
}
