package cse.java2.project.model;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;


/**
 * 一个拿来转JSON的类，调用useAPI能读取Question的JSON数据（其实就是items的部分），如果想要读其他类型的数据的话需要自己定义一下service
 * 如果只是想往question里加字段的话，直接去question类里面定义就好
 */
public class Questions {

    ArrayList<Question> items;


    public static List<Question> useAPI() {
        ArrayList<Question> questionArrayList = new ArrayList<>();
        try {
            String firstPort = "https://api.stackexchange.com/2.3/questions?page=1&pagesize=100&order=desc&sort=activity&tagged=java&site=stackoverflow&filter=!19uZVHXWOfxR(tG--Pd9Y-(t2k6sGkEtYb5KA.Zla4GfkH";
            String key = "&key=4RJi5zpqsZDDEAyx1ab73g((";
            URL url = new URL(firstPort + key);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept-Encoding", "gzip");
            BufferedReader in = new BufferedReader(
                new InputStreamReader(new GZIPInputStream(con.getInputStream()),
                    StandardCharsets.UTF_8));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
                content.append(inputLine);
            }
            in.close();
            Gson gson = new Gson();
            Questions questions = gson.fromJson(content.toString(), Questions.class);
            questionArrayList = questions.items;
            System.out.println(questions.items.size());
            //System.out.println(content.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return questionArrayList;
    }

}
