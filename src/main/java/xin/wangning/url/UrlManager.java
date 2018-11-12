package xin.wangning.url;

import java.util.*;

public class UrlManager {
    List<String> oldUrl;
    List<String> newUrl;

    public UrlManager(){
        oldUrl = new ArrayList<String>();
        newUrl = new ArrayList<String>();
    }

    public void addUrls(List<String> urls){
        for(String url:urls){
            if(!newUrl.contains(url)&&!oldUrl.contains(url)){
                System.out.println("添加url:"+url);
                newUrl.add(url);
            }
        }
    }

    public String getUrl(){
        if (newUrl.isEmpty()){
            return "";
        }
        String url = newUrl.get(0);
        oldUrl.add(url);
        newUrl.remove(0);
        return url;
    }
}
