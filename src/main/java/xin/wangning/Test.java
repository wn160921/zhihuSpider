package xin.wangning;

import org.apache.ibatis.session.SqlSession;
import xin.wangning.domain.Question;
import xin.wangning.mapper.QuestionMapper;

import java.util.ArrayList;
import java.util.Date;


import static xin.wangning.util.MyUtil.getSqlSessionFactory;

public class Test {
    public static void main(String[] args)  {
//        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            SqlSession sqlSession = getSqlSessionFactory().openSession();
            QuestionMapper questionMapper = sqlSession.getMapper(QuestionMapper.class);
            Question question=new Question();
            question.setTitle("test");
            question.setUrl("test");
            question.setFocusNum(1);
            question.setContent("123");
            question.setScanNum(1);
            question.setDateCreate(new Date());
            question.setDateModify(new Date());
            questionMapper.insertQuestion(question);
            System.out.println(question.getId());
            sqlSession.commit();
            sqlSession.close();
            System.out.println(question.getId());
    }

    public static void testList(ArrayList<String> abc){
        abc.add("nih");
    }
}
