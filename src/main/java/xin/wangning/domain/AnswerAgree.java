package xin.wangning.domain;

public class AnswerAgree {
    private Long answerId;
    private Long userId;

    public AnswerAgree(){};
    public AnswerAgree(Long answerId,Long userId){
        this.answerId = answerId;
        this.userId = userId;
    }

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
