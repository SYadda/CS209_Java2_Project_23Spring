package cse.java2.project;

import cse.java2.project.model.Question;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class ApplicationTests {


    @Autowired
    JdbcTemplate jdbcTemplate;
    @Test
    void contextLoads() {
        List<Long> a = jdbcTemplate.queryForList("select account_id from question",Long.class);
        a.forEach(System.out::println);
    }

}
