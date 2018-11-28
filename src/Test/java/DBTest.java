import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import xin.wangning.domain.Question;
import xin.wangning.mapper.QuestionMapper;

import java.util.Date;

import static xin.wangning.util.MyUtil.getSqlSessionFactory;

public class DBTest {

    @Test
    public void testDataTable() {
        SqlSession sqlSession = getSqlSessionFactory().openSession();
        QuestionMapper questionMapper = sqlSession.getMapper(QuestionMapper.class);
//        List<Question> questionList = questionMapper.selectAll();
//        for (Question question:questionList){
//            System.out.println(question.getTitle());
//        }
        Question question=new Question();
        question.setTitle("test");
        question.setUrl("test");
        question.setFocusNum(1);
        question.setContent("123");
        question.setScanNum(1);
        question.setDateCreate(new Date());
        question.setDateModify(new Date());
        questionMapper.insertQuestion(question);
        sqlSession.commit();
        sqlSession.close();
    }
}
