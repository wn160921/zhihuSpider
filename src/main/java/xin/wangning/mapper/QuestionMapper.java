package xin.wangning.mapper;

import xin.wangning.domain.Question;

import java.util.List;

public interface  QuestionMapper {
    public Question selectById(String id);
    public void insertQuestion(Question question);
    public List<Question> selectAll();
}
