package com.lppnb.bysj.judgeservice.judge.strategy;

/**
 * 默认判题策略
 */
public class DefaultJudgeStrategy extends AbstractJudgeStrategy {

    @Override
    protected Long getAdjustedMemoryLimit(Long memoryLimit) {
        return memoryLimit;
    }

    @Override
    protected Long getAdjustedTimeLimit(Long timeLimit) {
        return timeLimit;
    }
}