package xin.wangning;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Test {
    public static void main(String[] args){
        ArrayList<String> sxx = new ArrayList<>();
        testList(sxx);
        for(String s:sxx){
            System.out.println(s);
        }
    }

    public static void testList(ArrayList<String> abc){
        abc.add("nih");
    }
}
