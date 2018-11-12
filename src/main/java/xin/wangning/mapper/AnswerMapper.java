package xin.wangning.mapper;

import xin.wangning.domain.Answer;

import java.util.List;

public interface AnswerMapper {
    public List<Answer> selectAll();
    public Answer selectById(String id);
    public List<Answer> selectByQuestionId(int questionid);
    public void insertAnswer(Answer answer);
}
