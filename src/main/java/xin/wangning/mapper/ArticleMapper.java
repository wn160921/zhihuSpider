package xin.wangning.mapper;

import xin.wangning.domain.Article;

import java.util.List;

public interface ArticleMapper {
    public void insertArticle(Article article);
    public List<Article> selectAll();
}