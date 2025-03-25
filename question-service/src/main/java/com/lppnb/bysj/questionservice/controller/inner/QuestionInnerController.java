package com.lppnb.bysj.questionservice.controller.inner;

import com.lppnb.bysj.QuestionFeignClient;
import com.lppnb.bysj.entity.Question;
import com.lppnb.bysj.entity.QuestionSubmit;
import com.lppnb.bysj.questionservice.service.QuestionService;
import com.lppnb.bysj.questionservice.service.QuestionSubmitService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 该服务仅内部调用，不是给前端的
 */
@RestController
@RequestMapping("/inner")
public class QuestionInnerController implements QuestionFeignClient {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @GetMapping("/get/id")
    @Override
    public Question getQuestionById(@RequestParam("questionId") long questionId) {
        return questionService.getById(questionId);
    }

    @PostMapping("/update")
    @Override
    public boolean updateQuestionById(Question question) {
        return questionService.updateById(question);
    }

    @GetMapping("/question_submit/get/id")
    @Override
    public QuestionSubmit getQuestionSubmitById(@RequestParam("questionId") long questionSubmitId) {
        return questionSubmitService.getById(questionSubmitId);
    }

    @PostMapping("/question_submit/update")
    @Override
    public boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit) {
        return questionSubmitService.updateById(questionSubmit);
    }

}
