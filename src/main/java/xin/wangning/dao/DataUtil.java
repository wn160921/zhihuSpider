package xin.wangning.dao;

import com.alibaba.fastjson.JSON;
import xin.wangning.domain.MyData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DataUtil {
    public static void storage(MyData data){
        String dataJson = JSON.toJSONString(data);
        File file = new File("data.json");
        try {
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(dataJson);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
