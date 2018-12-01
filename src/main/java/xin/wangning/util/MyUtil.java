package xin.wangning.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import xin.wangning.domain.Answer;
import xin.wangning.domain.Article;
import xin.wangning.domain.ArticleDiscuss;
import xin.wangning.domain.Question;
import xin.wangning.mapper.AnswerMapper;
import xin.wangning.mapper.ArticleDiscussMapper;
import xin.wangning.mapper.ArticleMapper;
import xin.wangning.mapper.QuestionMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyUtil {
    private static SqlSessionFactory sqlSessionFactory = null;

    public static SqlSessionFactory getSqlSessionFactory(){
        if(sqlSessionFactory!=null){
            return sqlSessionFactory;
        }else {
            String resource = "mybatis-config.xml";
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

    public static void dumpArticle(Article article){
        SqlSession sqlSession = getSqlSessionFactory().openSession();
        ArticleMapper articleMapper = sqlSession.getMapper(ArticleMapper.class);
        articleMapper.insertArticle(article);
        ArticleDiscussMapper articleDiscussMapper = sqlSession.getMapper(ArticleDiscussMapper.class);
        for(ArticleDiscuss discuss:article.getArticleDiscussList()){
            discuss.setArticleId(article.getId());
            articleDiscussMapper.insertArticleDiscuss(discuss);
        }
        sqlSession.commit();
        sqlSession.close();
    }

    public static String subDateStr(String str){
        Pattern pattern = Pattern.compile("[0-9]{4}[-][0-9]{1,2}[-][0-9]{1,2}");
        Matcher matcher = pattern.matcher(str);
        String dateStr = null;
        if(matcher.find()){
            dateStr = matcher.group(0);
        }
        return dateStr;

    }
}
