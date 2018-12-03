package xin.wangning.parser;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import xin.wangning.domain.Answer;
import xin.wangning.domain.Question;
import xin.wangning.domain.User;
import xin.wangning.util.MyUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * 使用selenium swebdriver自带的解析api进行解析
 */
public class DriverParser {
    private ChromeDriver driver;
    private DateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd");


    public DriverParser(ChromeDriver driver) {
        this.driver = driver;
    }


    public List<String> getAllQuestionAndArticleUrl(int aimNum) {
        List<String> urlList = new ArrayList<>();
        scrollItems(".List-item.TopicFeedItem", aimNum);
        List<WebElement> linkElements = driver.findElements(By.cssSelector("meta[itemprop$=url]"));
        for (WebElement element : linkElements) {

            String url = element.getAttribute("content");
            if (url.contains("/question/") || url.contains("/p/")) {
                url = url.replaceAll("/answer/.*", "");
                urlList.add(url);
            }
        }
        return urlList;
    }

    public Question parserQuestion() {
        Question question = new Question();

        WebElement titleElement = driver.findElement(By.cssSelector(".QuestionHeader-title"));
        question.setTitle(titleElement.getAttribute("innerHTML"));

        WebElement dateCreateElem = driver.findElement(By.cssSelector("meta[itemprop$=dateCreated]"));
        try {
            question.setDateCreate(myDateFormat.parse(MyUtil.subDateStr(dateCreateElem.getAttribute("content"))));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        WebElement dateModifyElem = driver.findElement(By.cssSelector("meta[itemprop$=dateModified]"));
        try {
            question.setDateModify(myDateFormat.parse(MyUtil.subDateStr(dateModifyElem.getAttribute("content"))));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //查找被关注和被浏览
        List<WebElement> scanAndFocusElem = driver.findElements(By.className("NumberBoard-itemValue"));
        int index = 1;
        for (WebElement e : scanAndFocusElem) {
            if (index == 1) {
                question.setFocusNum(Integer.parseInt(e.getAttribute("title")));
                index++;
            } else {
                question.setScanNum(Integer.parseInt(e.getAttribute("title")));
            }
        }

        List<WebElement> listItemList = driver.findElementsByClassName("List-item");
        List<Answer> answers = new ArrayList<>();
        question.setAnswerList(answers);
        letAllAnswerShow();
        for (WebElement e : listItemList) {
            Answer answer = new Answer();
            try {
                WebElement userInfoElem = e.findElement(By.cssSelector(".AuthorInfo.AnswerItem-authorInfo.AnswerItem-authorInfo--related"));
                WebElement nameElem = userInfoElem.findElement(By.cssSelector("meta[itemprop$=name]"));
                answer.setAuthor(nameElem.getAttribute("content"));

                WebElement urlElem = userInfoElem.findElement(By.cssSelector("meta[itemprop$=url]"));
                answer.setAuthorUrl(urlElem.getAttribute("content"));

                WebElement agreeElem = e.findElement(By.cssSelector(".Button.VoteButton.VoteButton--up"));
                String agreeNumStr = agreeElem.getText().trim().replaceAll(" |赞同", "");
                int agreeNum = getAgreeNum(agreeNumStr);
                answer.setAgreeNum(agreeNum);

                WebElement contentElem = e.findElement(By.cssSelector(".RichText.ztext.CopyrightRichText-richText"));
                answer.setContent(contentElem.getText());

                WebElement discussElem = e.findElement(By.cssSelector(".Button.ContentItem-action.Button--plain.Button--withIcon.Button--withLabel"));
                String discussText = discussElem.getText().trim().replaceAll("[ 条评论添加]", "");
                if ("".equals(discussText)) {
                    answer.setDiscussNum(0);
                } else {
                    answer.setDiscussNum(Integer.parseInt(discussText));
                }

                //获取赞同信息
                try {
                    WebElement agreeShowBtn = e.findElement(By.cssSelector("Button.Button--plain"));
                    agreeShowBtn.sendKeys(Keys.ENTER);
                    List<WebElement> voteList = getAllAgreePeople(answer.getAgreeNum());
                    List<User> voteUserList = new ArrayList<>();
                    answer.setAgreeUser(voteUserList);
                    for(WebElement voteElem:voteList){
                        WebElement voteUserInfoElem = voteElem.findElement(By.className("UserLink-link"));
                        String voteUserName = voteUserInfoElem.getText();
                        String voteUserUrl = voteUserInfoElem.getAttribute("href");
                        User voteUser = new User();
                        voteUser.setName(voteUserName);
                        voteUser.setUrl(voteUserUrl);
                        voteUserList.add(voteUser);
                    }

                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                answers.add(answer);
            } catch (Exception e2) {
                e2.printStackTrace();
                continue;
            }
        }

        return question;
    }

    private List<WebElement> getAllAgreePeople(int agreeNum) {
        WebElement voterContent = driver.findElement(By.className("VoterList-content"));
        List<WebElement> voteList = voterContent.findElements(By.className("List-item"));
        int length = voteList.size();
        while (length<agreeNum){
            String jsDown = "window.scrollTo(0,document.body.scrollHeight);";
            try {
                ((JavascriptExecutor) driver).executeScript(jsDown);
                Thread.sleep(500);
                voteList = driver.findElements(By.className("List-item"));
                length = voteList.size();
                if (voteList.size() >= agreeNum) {
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return voteList;
    }


    private int getAgreeNum(String agreeNumStr) {
        int agreeNum = 0;
        if (!"".equals(agreeNumStr)) {
            if (agreeNumStr.contains("K")) {
                agreeNumStr = agreeNumStr.replaceAll("\\.", "").replaceAll("K|K", "000");
            }
            agreeNum = Integer.parseInt(agreeNumStr);
        }
        return agreeNum;
    }

    /**
     * 设置滚动出多少条
     *
     * @param cssSelector
     */
    private void scrollItems(String cssSelector, int aimNum) {
        int sameTimes = 0;
        int length = 0;
        while (true) {
            String jsDown = "window.scrollTo(0,document.body.scrollHeight);";
            String jsUp = "window.scrollTo(0,0);";
            try {
                ((JavascriptExecutor) driver).executeScript(jsUp);
                ((JavascriptExecutor) driver).executeScript(jsDown);
                Thread.sleep(1000);
                List<WebElement> elements = driver.findElements(By.cssSelector(cssSelector));
                if (length == elements.size()) {
                    sameTimes++;
                    if (sameTimes > aimNum) {
                        break;
                    }
                }
                length = elements.size();
                if (elements.size() >= aimNum) {
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void letAllAnswerShow() {
        try {
            WebElement allAnswerElem = driver.findElement(By.cssSelector(".QuestionMainAction"));
            allAnswerElem.sendKeys(Keys.ENTER);
            Thread.sleep(1000);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        while (true) {
            String jsDown = "window.scrollTo(0,document.body.scrollHeight);";
            String jsUp = "window.scrollTo(0,0);";
            try {
                ((JavascriptExecutor) driver).executeScript(jsUp);
                ((JavascriptExecutor) driver).executeScript(jsDown);
                Thread.sleep(1000);
                List<WebElement> elements = driver.findElements(By.cssSelector(".Button.QuestionAnswers-answerButton.Button--blue.Button--spread"));
                if (elements.size() > 0) {
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
