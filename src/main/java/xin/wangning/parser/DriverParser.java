package xin.wangning.parser;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 使用selenium swebdriver自带的解析api进行解析
 */
public class DriverParser {
    private ChromeDriver driver;

    public DriverParser(ChromeDriver driver){
        this.driver = driver;
    }


    public List<String> getAllQuestionAndArticleUrl(int aimNum){
        List<String> urlList = new ArrayList<>();
        scrollItems(".List-item.TopicFeedItem",aimNum);
        List<WebElement> linkElements = driver.findElements(By.cssSelector("meta[itemprop$=url]"));
        for(WebElement element:linkElements){
            String url = element.getAttribute("content");
            if(url.contains("/question/") || url.contains("/p/")){
                url = url.replaceAll("/answer/.*","");
                urlList.add(url);
            }
        }
        return urlList;
     }

    /**
     * 设置滚动出多少条
     * @param cssSelector
     */
    private void scrollItems(String cssSelector,int aimNum){
        while (true) {
            String jsDown = "window.scrollTo(0,document.body.scrollHeight);";
            String jsUp = "window.scrollTo(0,0);";
            try {
                ((JavascriptExecutor) driver).executeScript(jsUp);
                ((JavascriptExecutor) driver).executeScript(jsDown);
                Thread.sleep(1000);
                List<WebElement> elements = driver.findElements(By.cssSelector(cssSelector));
                if (elements.size() >= aimNum) {
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
