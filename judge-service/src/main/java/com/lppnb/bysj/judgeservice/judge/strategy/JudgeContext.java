package com.lppnb.bysj.judgeservice.judge.strategy;

import com.lppnb.bysj.codesandbox.JudgeInfo;
import com.lppnb.bysj.dto.question.JudgeCase;
import com.lppnb.bysj.entity.Question;
import com.lppnb.bysj.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 上下文（用于定义在策略中传递的参数）
 */
@Data
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private String message;

    private Integer status;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;

}
