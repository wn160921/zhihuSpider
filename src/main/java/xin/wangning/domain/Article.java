package xin.wangning.domain;

import java.util.Date;
import java.util.List;

public class Article {
    Long id;
    String title;
    String articleUrl;
    String author;
    String authorUrl;
    int agreeNum;
    String content;
    int discussNum;
    List<ArticleDiscuss> articleDiscussList;
    Date dateModify;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    public int getAgreeNum() {
        return agreeNum;
    }

    public void setAgreeNum(int agreeNum) {
        this.agreeNum = agreeNum;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getDiscussNum() {
        return discussNum;
    }

    public void setDiscussNum(int discussNum) {
        this.discussNum = discussNum;
    }

    public List<ArticleDiscuss> getArticleDiscussList() {
        return articleDiscussList;
    }

    public void setArticleDiscussList(List<ArticleDiscuss> articleDiscussList) {
        this.articleDiscussList = articleDiscussList;
    }

    public Date getDateModify() {
        return dateModify;
    }

    public void setDateModify(Date dateModify) {
        this.dateModify = dateModify;
    }
}
