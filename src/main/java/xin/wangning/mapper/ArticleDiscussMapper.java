package xin.wangning.mapper;

import xin.wangning.domain.ArticleDiscuss;

import java.util.List;

public interface ArticleDiscussMapper {
    public void insertArticleDiscuss(ArticleDiscuss articleDiscuss);
    public List<ArticleDiscuss> selectAll();
}
