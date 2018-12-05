package xin.wangning.control;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import xin.wangning.domain.Article;
import xin.wangning.domain.MyData;
import xin.wangning.domain.Question;
import xin.wangning.parser.DriverParser;
import xin.wangning.parser.HTMLParser;
import xin.wangning.url.UrlManager;
import xin.wangning.util.MyUtil;

public class Controller {
    static ChromeDriver driver = null;
    static Scanner scanner = null;

    public static UrlManager urlManager;
    public static HTMLParser htmlParser;
    public static DriverParser driverParser;
    public static MyData myData;

    public static void main(String[] args) {
        urlManager = new UrlManager();
        htmlParser = new HTMLParser();

        driver = getWebDriver();
        driverParser = new DriverParser(driver);
        boolean exitFlag = true;
        while (exitFlag) {
            print("输入操作");
            print("1、自动加载cookies登录");
            print("2、手动登录");
            print("3、退出");
            print("4、开始");
            scanner = new Scanner(System.in);
            int order = scanner.nextInt();
            switch (order) {
                case 1:
                    loadCookies();
                    driver.get("https://www.zhihu.com");
                    break;
                case 2:
                    loginByHand();
                    saveCookies();
                    break;
                case 3:
                    driver.close();
                    exitFlag = false;
                    break;
                case 4:
                    crawl();
                    break;
                default:
                    break;
            }
        }
    }

    public static void print(String s) {
        System.out.println(s);
    }

    //仅供初始化用
    public static ChromeDriver getWebDriver() {
        ChromeDriver webDriver = new ChromeDriver();
        webDriver.manage().window().maximize();
        webDriver.get("https://www.baidu.com");
        return webDriver;
    }

    public static boolean loginByHand() {
        driver.get("https://www.zhihu.com");
        print("完成后输入0，其它出bug");
        int end = scanner.nextInt();
        if (end == Constant.LOGIN_COMPLETE) {
            return true;
        } else {
            return false;
        }
    }

    public static void saveCookies() {
        Set<Cookie> cookieSet = driver.manage().getCookies();
        String cookiesStr = JSON.toJSONString(cookieSet);
        File file = new File("cookies.json");
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fw);
            writer.write(cookiesStr);
            writer.flush();
            writer.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadCookies() {
        File file = new File("cookies.json");
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            StringBuilder builder = new StringBuilder();
            String line = "";
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            String cookiesStr = builder.toString();
            List<Cookie> cookieSet = JSON.parseArray(cookiesStr, Cookie.class);
            for (Cookie cookie : cookieSet) {
                driver.manage().addCookie(cookie);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void crawl() {
        driver.get("https://www.zhihu.com/topic/19631819/newest");
//        driver.get("https://www.zhihu.com/topic/19550901/hot");
//        getAllQuestionShow(20);
        List<String> urls = driverParser.getAllQuestionAndArticleUrl(200);
        urlManager.addUrls(urls);
        String url;
        while (!(url = urlManager.getUrl()).equals("")) {
            if (url.contains("/p/")) {
//                driver.get(url);
//                Article article = htmlParser.parseArticle(driver.getPageSource());
//                article.setArticleUrl(url);
//                MyUtil.dumpArticle(article);
            } else {
                driver.get(url);
//                getAllAnswerShow();
//                Question question = htmlParser.parseQuestion(driver.getPageSource());
                Question question = driverParser.parserQuestion();
                question.setUrl(url);
                MyUtil.dumpQuestion(question);
//                myData.addQuestion(question);
            }
        }
//        DataUtil.storage(myData);
    }

    public static void getAllQuestionShow(int num) {
        while (true) {
            String jsDown = "window.scrollTo(0,document.body.scrollHeight);";
            String jsUp = "window.scrollTo(0,0);";
            try {
                ((JavascriptExecutor) driver).executeScript(jsUp);
                ((JavascriptExecutor) driver).executeScript(jsDown);
                Thread.sleep(1000);
                List<WebElement> elements = driver.findElements(By.className("List-item"));
                if (elements.size() >= num) {
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void getAllAnswerShow() {
        try {
            WebElement allAnswerElem = driver.findElement(By.className("QuestionMainAction"));
            allAnswerElem.sendKeys(Keys.ENTER);
//            allAnswerElem.click();
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
                List<WebElement> elements = driver.findElements(By.cssSelector(".QuestionAnswers-answerButton"));
                if (elements.size() > 0) {
                    break;
                }
//                .QuestionAnswers-answerButton
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    //滚动使加载所有的问题
    public static void mainScroll(String className, int minTimes) {
        //先点击几个按钮加载所有的答案或者评论
        try {
            WebElement allAnswerElem = driver.findElement(By.className("QuestionMainAction"));
            allAnswerElem.click();
            Thread.sleep(1000);
        } catch (Exception e) {

        }
        List<WebElement> elements = driver.findElements(By.className(className));
        int length = elements.size();
        while (true) {
            String jsDown = "window.scrollTo(0,document.body.scrollHeight);";
            String jsUp = "window.scrollTo(0,0);";
            try {
                ((JavascriptExecutor) driver).executeScript(jsUp);
//                Thread.sleep(1000);
                ((JavascriptExecutor) driver).executeScript(jsDown);
                Thread.sleep(4000);
                List<WebElement> elements2 = driver.findElements(By.className(className));
                if (length == elements2.size()) {
                    break;
                } else if (elements2.size() >= minTimes) {
                    break;
                } else {
                    length = elements2.size();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
