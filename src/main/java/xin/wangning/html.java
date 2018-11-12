package xin.wangning;

import java.io.*;
import java.net.URL;
 
public class html {
   public static void main(String[] args) throws Exception {
	  File f=new File("data.txt");
      URL url = new URL("https://jobs.zhaopin.com/CC000304002J00086489015.htm");
      BufferedReader reader = new BufferedReader (new InputStreamReader(url.openStream(),"UTF-8"));
      //OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(f),"UTF-8");
      BufferedWriter writer = new BufferedWriter (new OutputStreamWriter(new FileOutputStream(f),"GBK"));
      String line;
      while ((line = reader.readLine()) != null) {
    	 //System.out.println(line);
         writer.write(line);
         writer.newLine();
      }
      reader.close();
      writer.flush();
      writer.close();
   }
}