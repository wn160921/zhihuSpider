package xin.wangning;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Test {
    public static void main(String[] args){
//        WebDriver driver = new ChromeDriver();
//        driver.get("https://www.baidu.com");
//        Set<Cookie> cookieSet = driver.manage().getCookies();
//        Logger log = Logger.getLogger("test");
//        log.setLevel(Level.INFO);
//        log.info("info test");
//        log.warning("warn test");
//        log.setLevel(Level.WARNING);
//        log.info("info2");
//        log.warning("warn2");
        String a = "1K";
        System.out.println(a.replaceAll("\\.","").replaceAll("K","000"));
    }
}
