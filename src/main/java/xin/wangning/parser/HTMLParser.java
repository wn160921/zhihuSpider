package xin.wangning.parser;
import com.alibaba.fastjson.JSON;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import xin.wangning.domain.Answer;
import xin.wangning.domain.Article;
import xin.wangning.domain.ArticleDiscuss;
import xin.wangning.domain.Question;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class HTMLParser {

    public Question parseQuestion(String html){
        Question question = new Question();
        Document doc = Jsoup.parse(html);
        String title = doc.getElementsByClass("QuestionHeader-title").first().text();
        question.setTitle(title);
        Elements listItem = doc.getElementsByClass("List-item");
        List<Answer> answerList = new ArrayList<Answer>();
        for(Element element:listItem){
            Answer answer = new Answer();
            Element userInfoElem = element.getElementsByClass("AuthorInfo AnswerItem-authorInfo AnswerItem-authorInfo--related").first();
            Element nameElem = userInfoElem.selectFirst("meta[itemprop$=name]");
            String name = nameElem.attr("content");
            answer.setName(name);
            String url = userInfoElem.selectFirst("meta[itemprop$=url]").attr("content");
            answer.setAuthorUrl(url);
            Element agreeElem = element.getElementsByClass("Button VoteButton VoteButton--up").first();
            int agreeNum = Integer.parseInt(agreeElem.text().trim().replaceAll(" |赞同",""));
            answer.setAgreeNum(agreeNum);

            Element contentElem = element.getElementsByClass("RichText ztext CopyrightRichText-richText").first();
            String content = contentElem.text();
            answer.setAnswerContent(content);

            Element discussElem = element.getElementsByClass("Button ContentItem-action Button--plain Button--withIcon Button--withLabel").first();
            String discussText = discussElem.text().trim().replaceAll("[ 条评论添加]","");
            if("".equals(discussText)){
                answer.setDiscussNum(0);
            }else {
                answer.setDiscussNum(Integer.parseInt(discussText));
            }
            answerList.add(answer);
            question.setAnswerList(answerList);
        }
        return question;
    }

    public Article parseArticle(String htmlDoc){
        Article article = new Article();
        Document doc = Jsoup.parse(htmlDoc);
        Element titleElem = doc.getElementsByClass("Post-Title").first();
        String title = titleElem.text();
        article.setTitle(title);

        Element authorInfoElem = doc.getElementsByClass("AuthorInfo").first();
        String authorName = authorInfoElem.selectFirst("meta[itemprop$=name]").attr("content");
        String authorUrl = authorInfoElem.selectFirst("meta[itemprop$=url]").attr("content");
        article.setAuther(authorName);
        article.setAutherUrl(authorUrl);

        Element contentElem = doc.getElementsByClass("RichText ztext Post-RichText").first();
        String content = contentElem.text();
        article.setContent(content);

        Element agreeElem = doc.getElementsByClass("Button VoteButton VoteButton--up").first();
        int agreeNum = Integer.parseInt(agreeElem.text().trim().replaceAll(" |赞同",""));
        article.setAgreeNum(agreeNum);

        Element discussElem = doc.getElementsByClass("Button BottomActions-CommentBtn Button--plain Button--withIcon Button--withLabel").first();
        String discussText = discussElem.text().trim().replaceAll("[ 条评论添加]","");
        if("".equals(discussText)){
            article.setDiscussNum(0);
        }else {
            article.setDiscussNum(Integer.parseInt(discussText));
        }
        List<ArticleDiscuss> articleDiscussList = new LinkedList<ArticleDiscuss>();
        Elements discussElems = doc.getElementsByClass("CommentItem");
        for(Element elem:discussElems){
            ArticleDiscuss articleDiscuss = new ArticleDiscuss();
            Element userLinkElem = elem.getElementsByClass("UserLink-link").first();
            String url = userLinkElem.attr("href");
            articleDiscuss.setAuthorUrl(url);

            Element nameElem = userLinkElem.getElementsByTag("img").first();
            String name = nameElem.attr("alt");
            articleDiscuss.setAutherName(name);

            Element discussContentElem = elem.getElementsByClass("RichText ztext CommentItem-content").first();
            String discussContent = discussContentElem.text();
            articleDiscuss.setContent(discussContent);

            Element discussAgreeElem = elem.getElementsByClass("Button CommentItem-likeBtn Button--plain").first();
            if(discussAgreeElem==null){
                discussAgreeElem = elem.getElementsByClass("Button CommentItem-likeBtn is-liked Button--plain").first();
            }
            String discussAgree = discussAgreeElem.text().replaceAll("赞","");
            if("".equals(discussAgree)){
                articleDiscuss.setAgree(0);
            }else {
                articleDiscuss.setAgree(Integer.parseInt(discussAgree));
            }
            articleDiscussList.add(articleDiscuss);

        }
        article.setArticleDiscussList(articleDiscussList);
        return article;
    }

    public List<String> getAllQuestionUrl(String htmlDoc){
        Document doc = Jsoup.parse(htmlDoc);
        List<String> urlList = new ArrayList<String>();

        Elements questionLink = doc.getElementsByClass("List-item TopicFeedItem");
        for(Element e:questionLink){
            String url = "https://www.zhihu.com/"+e.getElementsByTag("a").first().attr("href");
            urlList.add(url);
        }
        return urlList;
    }

    @Test
    public void testQuestionP() throws Exception {
        File htmlFile = new File("question.htm");
        BufferedReader reader = new BufferedReader(new FileReader(htmlFile));
        StringBuilder builder = new StringBuilder();
        String temp;
        while((temp=reader.readLine())!=null){
            builder.append(temp);
        }
        String html_doc = builder.toString();
        reader.close();
        Question question = parseQuestion(html_doc);
        System.out.println(JSON.toJSONString(question));
    }

    @Test
    public void testArtilcleP() throws Exception {
        File htmlFile = new File("artilce.htm");
        BufferedReader reader = new BufferedReader(new FileReader(htmlFile));
        StringBuilder builder = new StringBuilder();
        String temp;
        while((temp=reader.readLine())!=null){
            builder.append(temp);
        }
        String html_doc = builder.toString();
        reader.close();
        Article article = parseArticle(html_doc);
        System.out.println(JSON.toJSONString(article));
    }
}
