package xin.wangning.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import xin.wangning.domain.Answer;
import xin.wangning.domain.Question;
import xin.wangning.mapper.AnswerMapper;
import xin.wangning.mapper.QuestionMapper;

import java.io.IOException;
import java.io.InputStream;

public class MyUtil {
    private static SqlSessionFactory sqlSessionFactory = null;

    public static SqlSessionFactory getSqlSessionFactory(){
        if(sqlSessionFactory!=null){
            return sqlSessionFactory;
        }else {
            String resource = "sqlMapConfig.xml";
            InputStream inputStream = null;
            try {
                inputStream = Resources.getResourceAsStream(resource);
            } catch (IOException e) {
                e.printStackTrace();
            }
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            return sqlSessionFactory;
        }
    }

    public static void dumpQuestion(Question question){
        SqlSession sqlSession = getSqlSessionFactory().openSession();
        QuestionMapper questionMapper = sqlSession.getMapper(QuestionMapper.class);
        questionMapper.insertQuestion(question);
        AnswerMapper answerMapper = sqlSession.getMapper(AnswerMapper.class);
        for(Answer answer:question.getAnswerList()){
            answer.setQuestionID(question.getId());
            answerMapper.insertAnswer(answer);
        }
        sqlSession.commit();
        sqlSession.close();
    }
}
