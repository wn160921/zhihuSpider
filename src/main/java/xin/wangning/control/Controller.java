package xin.wangning.control;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.alibaba.fastjson.JSON;

public class Controller {
    static WebDriver driver = null;
    static Scanner scanner = null;
    public static void main(String[] args){
        driver = getWebDriver();
        boolean exitFlag = true;
        while (exitFlag){
            print("输入操作");
            print("1、自动加载cookies登录");
            print("2、手动登录");
            print("3、退出");
            scanner = new Scanner(System.in);
            int order = scanner.nextInt();
            switch (order){
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
            }
        }
    }

    public static void print(String s){
        System.out.println(s);
    }

    //仅供初始化用
    public static WebDriver getWebDriver(){
        WebDriver webDriver = new ChromeDriver();
        webDriver.get("https://www.baidu.com");
        return webDriver;
    }

    public static boolean loginByHand(){
        driver.get("https://www.zhihu.com");
        print("完成后输入0，其它出bug");
        int end = scanner.nextInt();
        if(end==Constant.LOGIN_COMPLETE){
            return true;
        }else {
            return false;
        }
    }

    public static void saveCookies(){
        Set<Cookie> cookieSet = driver.manage().getCookies();
        String cookiesStr = JSON.toJSONString(cookieSet);
        File file = new File("cookies.json");
        try {
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fw);
            writer.write(cookiesStr);
            writer.flush();
            writer.close();
            fw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void loadCookies(){
        File file = new File("cookies.json");
        try{
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            StringBuilder builder = new StringBuilder();
            String line="";
            while ((line=br.readLine())!=null){
                builder.append(line);
            }
            String cookiesStr = builder.toString();
            List<Cookie> cookieSet = JSON.parseArray(cookiesStr,Cookie.class);
            for(Cookie cookie:cookieSet){
                driver.manage().addCookie(cookie);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
