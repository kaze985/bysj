package com.lppnb.bysj.judgeservice.judge;

import cn.hutool.json.JSONUtil;
import com.lppnb.bysj.QuestionFeignClient;
import com.lppnb.bysj.codesandbox.ExecuteCodeRequest;
import com.lppnb.bysj.codesandbox.ExecuteCodeResponse;
import com.lppnb.bysj.codesandbox.JudgeInfo;
import com.lppnb.bysj.common.ErrorCode;
import com.lppnb.bysj.dto.question.JudgeCase;
import com.lppnb.bysj.entity.Question;
import com.lppnb.bysj.entity.QuestionSubmit;
import com.lppnb.bysj.enums.JudgeInfoMessageEnum;
import com.lppnb.bysj.enums.QuestionSubmitStatusEnum;
import com.lppnb.bysj.exception.BusinessException;
import com.lppnb.bysj.judgeservice.judge.codesandbox.CodeSandbox;
import com.lppnb.bysj.judgeservice.judge.codesandbox.CodeSandboxFactory;
import com.lppnb.bysj.judgeservice.judge.codesandbox.CodeSandboxProxy;
import com.lppnb.bysj.judgeservice.judge.strategy.JudgeContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionFeignClient questionFeignClient;

    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String type;


    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1）传入题目的提交 id，获取到对应的题目、提交信息（包含代码、编程语言等）
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionFeignClient.getQuestionById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        // 2）如果题目提交状态不为等待中，就不用重复执行了
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }
        // 3）更改判题（题目提交）的状态为 “判题中”，防止重复执行
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        // 4）调用沙箱，获取到执行结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        // 获取输入用例
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        List<String> outputList = executeCodeResponse.getOutputList();
        String message = executeCodeResponse.getMessage();
        Integer status = executeCodeResponse.getStatus();
        JudgeInfo judgeInfo = executeCodeResponse.getJudgeInfo();
        // 5）根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setMessage(message);
        judgeContext.setStatus(status);
        judgeContext.setJudgeInfo(judgeInfo);
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);
        JudgeInfo afterJudgeInfo = judgeManager.doJudge(judgeContext);
        // 6）修改数据库中的判题结果
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(status);
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(afterJudgeInfo));
        update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目提交状态更新错误");
        }
        // 7）修改题目提交数和通过数
        Question latestQuestion = questionFeignClient.getQuestionById(questionId);
        Integer latestQuestionSubmitNum = latestQuestion.getSubmitNum();
        Integer latestQuestionAcceptedNum = latestQuestion.getAcceptedNum();
        Question updateNumQuestion = new Question();
        updateNumQuestion.setId(questionId);
        updateNumQuestion.setSubmitNum(latestQuestionSubmitNum + 1);
        if (JudgeInfoMessageEnum.ACCEPTED.getValue().equals(afterJudgeInfo.getMessage())) {
            updateNumQuestion.setAcceptedNum(latestQuestionAcceptedNum + 1);
        }
        boolean updateNum = questionFeignClient.updateQuestionById(updateNumQuestion);
        if (!updateNum) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目提交数和通过数更新错误");
        }
        return questionFeignClient.getQuestionSubmitById(questionId);
    }
}
