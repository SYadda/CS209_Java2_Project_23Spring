package cse.java2.project.controller;


import cse.java2.project.serive.QuestionService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoRestController {

    private final QuestionService questionService;

    @Autowired
    public DemoRestController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/api/questionById")
    public String getQuestionByQuestionId(@RequestParam(value = "id") Optional<String> id) {
        if (id.isPresent()) {
            StringBuilder content = new StringBuilder();
            try {
                String firstPort = "https://api.stackexchange.com/2.3/questions/" + id.get()
                    + "?order=desc&sort=activity&site=stackoverflow";
                stackAPI(content, firstPort);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return content.toString();
        }
        return null;
    }

    private void stackAPI(StringBuilder content, String firstPort) throws IOException {
        String key = "&key=4RJi5zpqsZDDEAyx1ab73g((";
        URL url = new URL(firstPort + key);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept-Encoding", "gzip");
        BufferedReader in = new BufferedReader(
            new InputStreamReader(new GZIPInputStream(con.getInputStream()),
                StandardCharsets.UTF_8));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
    }


    @GetMapping("/api/questionByTags")
    public String getQuestionByTag(@RequestParam(value = "tags") Optional<String> tags) {
        if (tags.isPresent()) {
            StringBuilder content = new StringBuilder();
            try {
                String firstPort =
                    "https://api.stackexchange.com/2.3/questions?pagesize=10&order=desc&sort=activity&tagged=java;"
                        + tags.get() + "%3Bmysql&site=stackoverflow";
                stackAPI(content, firstPort);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return content.toString();
        }
        return null;
    }


    @GetMapping("/api/relate")
    public String getQuestionRelate(@RequestParam(value = "id") Optional<String> id) {
        if (id.isPresent()) {
            StringBuilder content = new StringBuilder();
            try {
                String firstPort = "https://api.stackexchange.com/2.3/questions/" + id.get()
                    + "/related?pagesize=10&order=desc&sort=activity&site=stackoverflow";
                stackAPI(content, firstPort);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return content.toString();
        }
        return null;
    }


}
