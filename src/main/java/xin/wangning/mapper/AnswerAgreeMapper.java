package xin.wangning.mapper;

import xin.wangning.domain.AnswerAgree;

import java.util.List;

public interface AnswerAgreeMapper {
    public List<AnswerAgree> selectAll();
    public void insert(AnswerAgree answerAgree);
}
