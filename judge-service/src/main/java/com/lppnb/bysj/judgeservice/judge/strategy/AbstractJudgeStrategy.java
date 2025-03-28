package com.lppnb.bysj.judgeservice.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.lppnb.bysj.codesandbox.JudgeInfo;
import com.lppnb.bysj.dto.question.JudgeCase;
import com.lppnb.bysj.dto.question.JudgeConfig;
import com.lppnb.bysj.entity.Question;
import com.lppnb.bysj.enums.JudgeInfoMessageEnum;
import com.lppnb.bysj.enums.QuestionSubmitStatusEnum;

import java.util.List;
import java.util.Optional;

/**
 * 抽象判题策略
 */
public abstract class AbstractJudgeStrategy implements JudgeStrategy {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        Integer status = judgeContext.getStatus();
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        Question question = judgeContext.getQuestion();

        Long memory = Optional.ofNullable(judgeInfo.getMemory()).orElse(0L);
        Long time = Optional.ofNullable(judgeInfo.getTime()).orElse(0L);
        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        judgeInfoResponse.setMemory(memory);
        judgeInfoResponse.setTime(time);

        // 代码沙箱执行失败
        if (QuestionSubmitStatusEnum.FAILED.getValue().equals(status)) {
            // 编译失败
            judgeInfoMessageEnum = JudgeInfoMessageEnum.COMPILE_ERROR;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }

        // 先判断沙箱执行的结果输出数量是否和预期输出数量相等
        if (outputList.size() != inputList.size()) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }

        // 依次判断每一项输出和预期输出是否相等
        for (int i = 0; i < judgeCaseList.size(); i++) {
            JudgeCase judgeCase = judgeCaseList.get(i);
            if (!judgeCase.getOutput().equals(outputList.get(i))) {
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                return judgeInfoResponse;
            }
        }

        // 判断题目限制
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long needMemoryLimit = judgeConfig.getMemoryLimit();
        Long needTimeLimit = judgeConfig.getTimeLimit();

        // 调用子类实现的方法获取调整后的限制
        Long adjustedMemoryLimit = getAdjustedMemoryLimit(needMemoryLimit);
        Long adjustedTimeLimit = getAdjustedTimeLimit(needTimeLimit);

        if (memory > adjustedMemoryLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }

        if (time > adjustedTimeLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }

        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        return judgeInfoResponse;
    }

    /**
     * 获取调整后的内存限制
     *
     * @param memoryLimit 原始内存限制
     * @return 调整后的内存限制
     */
    protected abstract Long getAdjustedMemoryLimit(Long memoryLimit);

    /**
     * 获取调整后的时间限制
     *
     * @param timeLimit 原始时间限制
     * @return 调整后的时间限制
     */
    protected abstract Long getAdjustedTimeLimit(Long timeLimit);
}