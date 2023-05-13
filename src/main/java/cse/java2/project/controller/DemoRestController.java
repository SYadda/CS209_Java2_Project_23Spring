package cse.java2.project.controller;


import cse.java2.project.serive.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/测试")
public class DemoRestController {
    private final QuestionService questionService;

    @Autowired
    public DemoRestController(QuestionService questionService){
        this.questionService = questionService;
    }






}
