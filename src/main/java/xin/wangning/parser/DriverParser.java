package xin.wangning.parser;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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

        List<WebElement> listItemList = driver.findElement(By.id("QuestionAnswers-answers")).findElements(By.className("List-item"));
        List<Answer> answers = new ArrayList<>();
        question.setAnswerList(answers);
        letAllAnswerShow();
        for (WebElement e : listItemList) {
            Answer answer = new Answer();
            try {
                WebElement userInfoElem = e.findElement(By.cssSelector(".AuthorInfo.AnswerItem-authorInfo.AnswerItem-authorInfo--related"));
                WebElement nameElem = userInfoElem.findElement(By.cssSelector("meta[itemprop$=name]"));
                answer.setAuthor(nameElem.getAttribute("content"));
                if(answer.getAuthor().equals("匿名用户")){
                    continue;
                }
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
                if (agreeNum > 0) {
                    System.out.println("start click");
                    WebElement agreeShowBtn = e.findElement(By.cssSelector(".AnswerItem-extraInfo .Voters .Button.Button--plain"));
                    trueClick(agreeShowBtn);
                    getAllAgreePeople(answer.getAgreeNum());
                    WebElement voterContent = driver.findElement(By.className("VoterList-content"));
                    List<WebElement> voteList = voterContent.findElements(By.cssSelector(".List-item"));
                    List<User> voteUserList = new ArrayList<>();
                    answer.setAgreeUser(voteUserList);
                    for (WebElement voteElem : voteList) {
                        WebDriverWait webDriverWait = new WebDriverWait(driver, 10);
                        try {
                            webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".ContentItem-head a")));
                            WebElement voteUserInfoElem = voteElem.findElement(By.cssSelector(".ContentItem-head a"));
                            String voteUserName = voteUserInfoElem.getText();
                            String voteUserUrl = voteUserInfoElem.getAttribute("href");
                            User voteUser = new User();
                            voteUser.setName(voteUserName);
                            voteUser.setUrl(voteUserUrl);
                            voteUserList.add(voteUser);
                        }catch (Exception e4){
                            System.out.println("userLink not found");
                            continue;
                        }
                    }
                    WebElement closeElem = driver.findElement(By.cssSelector(".Button.Modal-closeButton.Button--plain"));
                    trueClick(closeElem);
                    System.out.println("has closed");
                }
                answers.add(answer);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        return question;
    }

    private void getAllAgreePeople(int agreeNum) {
        WebDriverWait webDriverWait = new WebDriverWait(driver, 10);
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".VoterList-content")));
        WebElement voterContent = driver.findElement(By.className("VoterList-content"));
        List<WebElement> voteList = voterContent.findElements(By.className("List-item"));
        int length = voteList.size();
        while (length < agreeNum) {
//            String jsDown = "window.scrollTo(0,document.body.scrollHeight);";
            try {
                //body > div:nth-child(13) > div > span > div > div.Modal.Modal--fullPage > div > div > div > div.VoterList-content > div:nth-child(556)
//                ((JavascriptExecutor) driver).executeScript(jsDown);
                Actions actions = new Actions(driver);
                voteList = driver.findElements(By.className("List-item"));
                if (voteList.size() == 0) {
                    Thread.sleep(500);
                    continue;
                }
                actions.moveToElement(voteList.get(voteList.size() - 1)).perform();
//                voterContent.sendKeys(Keys.ARROW_DOWN);
                System.out.println("拉一次");
                Thread.sleep(500);
                length = voteList.size();
                if (voteList.size() >= agreeNum) {
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
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
            trueClick(allAnswerElem);
            Thread.sleep(1000);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        while (true) {
            String jsDown = "window.scrollTo(0,document.body.scrollHeight);";
            String jsUp = "window.scrollTo(0,0);";
            try {
                driver.executeScript(jsUp);
                driver.executeScript(jsDown);
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

    private void trueClick(WebElement element) {
        element.sendKeys(Keys.ARROW_UP);
        System.out.println(element.getText());
        Actions actions = new Actions(driver);
//        driver.executeScript("arguments[0].scrollIntoView(true);",element);
        actions.moveToElement(element).click(element).perform();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
