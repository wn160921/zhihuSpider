import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import xin.wangning.domain.Answer;
import xin.wangning.domain.Question;
import xin.wangning.mapper.AnswerMapper;
import xin.wangning.mapper.QuestionMapper;

import static xin.wangning.util.MyUtil.getSqlSessionFactory;

public class DBTest {

    @Test
    public void testDataTable() {
        SqlSession sqlSession = getSqlSessionFactory().openSession();
        QuestionMapper questionMapper = sqlSession.getMapper(QuestionMapper.class);
        questionMapper.selectAll();
        sqlSession.commit();
        sqlSession.close();
    }
}
