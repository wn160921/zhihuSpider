package xin.wangning.domain;

import java.util.ArrayList;
import java.util.List;

public class Answer {
    Long id;
    Long questionID;
    String author;
    String authorUrl;
    int agreeNum;
    String content;
    int discussNum;
    List<User> agreeUser = new ArrayList<>();

    public List<User> getAgreeUser() {
        return agreeUser;
    }

    public void setAgreeUser(List<User> agreeUser) {
        this.agreeUser = agreeUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestionID() {
        return questionID;
    }

    public void setQuestionID(Long questionID) {
        this.questionID = questionID;
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

    public int getDiscussNum() {
        return discussNum;
    }

    public void setDiscussNum(int discussNum) {
        this.discussNum = discussNum;
    }
}
