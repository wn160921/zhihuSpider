package xin.wangning.control;

import org.apache.ibatis.session.SqlSession;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import xin.wangning.domain.Answer;
import xin.wangning.domain.Question;
import xin.wangning.mapper.AnswerMapper;
import xin.wangning.mapper.QuestionMapper;
import xin.wangning.util.MyUtil;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LuceneTest {

    public List<Question> getQuestionList(){
        SqlSession sqlSession = MyUtil.getSqlSessionFactory().openSession();
        QuestionMapper questionMapper = sqlSession.getMapper(QuestionMapper.class);
        AnswerMapper answerMapper = sqlSession.getMapper(AnswerMapper.class);
        List<Question> questionList = questionMapper.selectAll();
        for(Question q:questionList){
            List<Answer> answerList = answerMapper.selectByQuestionId(q.getId().intValue());
            q.setAnswerList(answerList);
        }
        sqlSession.commit();
        sqlSession.close();
        return questionList;
    }


    @Test
    public void createIndex() throws Exception{
        List<Question> questionList = getQuestionList();

        List<Document> documents = new ArrayList<Document>();
        for(Question q:questionList){
            Document document = new Document();
            Field id = new TextField("id", String.valueOf(q.getId()),Field.Store.YES);
            Field title = new TextField("title",q.getTitle(),Field.Store.YES);
            Field url = new TextField("url",q.getUrl(),Field.Store.YES);
            Field scanNum = new TextField("csanNum",String.valueOf(q.getScanNum()),Field.Store.YES);
            Field date = new TextField("date",String.valueOf(q.getDateModify()),Field.Store.YES);
            document.add(id);
            document.add(title);
            document.add(url);
            document.add(scanNum);
            document.add(date);
            System.out.println(q.getTitle());
            documents.add(document);
        }
        //创建分词器
        Analyzer analyzer = new SmartChineseAnalyzer();

        //创建index writer
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        Directory directory = FSDirectory.open(Paths.get("./index/"));

        IndexWriter writer = new IndexWriter(directory,config);
        writer.addDocuments(documents);
        writer.commit();
        writer.close();
    }

    @Test
    public void createAnswerIndex() throws Exception{
        List<Question> questionList = getQuestionList();
        List<Document> documents = new ArrayList<Document>();
        for(Question q:questionList){
            List<Answer> answerList = q.getAnswerList();
            for(Answer answer:answerList){
                Document document = new Document();
//                System.out.println(answer.getAgreeNum());
                Field id = new TextField("id", String.valueOf(q.getId()),Field.Store.YES);
                Field question = new TextField("question",q.getTitle(),Field.Store.YES);
                Field author = new StringField("anthor",answer.getAuthor(),Field.Store.YES);
                Field authorUrl = new StoredField("authorUrl",answer.getAuthorUrl());
                Field content = new TextField("content",answer.getContent(),Field.Store.YES);
                Field agreeNum = new LongPoint("agreeNum",answer.getAgreeNum());
                Field agreeNumStore = new StoredField("agreeNumStored",answer.getAgreeNum());
                document.add(id);
                document.add(question);
                document.add(author);
                document.add(authorUrl);
                document.add(content);
                document.add(agreeNum);
                document.add(agreeNumStore);
                documents.add(document);
            }
        }
        //创建分词器
        Analyzer analyzer = new SmartChineseAnalyzer();
        //创建index writer
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        Directory directory = FSDirectory.open(Paths.get("./answer_index/"));
        IndexWriter writer = new IndexWriter(directory,config);
        writer.addDocuments(documents);
        writer.commit();
        writer.close();
    }


    @Test
    public void indexSearch() throws Exception{
        QueryParser parser = new QueryParser("content",new SmartChineseAnalyzer());
//        Query query = LongPoint.newRangeQuery("agreeNum",10000,100000);
//        Query query = new TermRangeQuery("agreenum",new BytesRef(1),new BytesRef(10000),true,true);
        Query query = parser.parse("前端");
        Directory directory = FSDirectory.open(Paths.get("./answer_index/"));
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs topDocs = searcher.search(query,5);
        System.out.println("lenth:"+topDocs.totalHits);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for(ScoreDoc d:scoreDocs){
            Document doc = searcher.doc(d.doc);
            System.out.println(doc.get("question"));
            System.out.println(doc.get("content"));
            System.out.println(doc.get("agreeNumStored"));
            System.out.println("----------------------------------------------------------------------------------------------------------------------------");
        }
        reader.close();
    }
}
