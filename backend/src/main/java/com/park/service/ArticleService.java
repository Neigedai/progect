package com.park.service;

import com.park.common.PageResult;
import com.park.dto.ArticleQueryDTO;
import com.park.entity.CmsArticle;

public interface ArticleService {
    PageResult<CmsArticle> listArticles(ArticleQueryDTO query);
    CmsArticle getArticleById(Long id);
    void createArticle(CmsArticle article);
    void updateArticle(CmsArticle article);
    void deleteArticle(Long id);
    void updateStatus(Long id, Integer status);
}
