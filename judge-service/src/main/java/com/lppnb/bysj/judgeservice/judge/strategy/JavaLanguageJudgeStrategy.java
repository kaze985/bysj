package com.lppnb.bysj.judgeservice.judge.strategy;

/**
 * Java 程序的判题策略
 */
public class JavaLanguageJudgeStrategy extends AbstractJudgeStrategy {

    // Java 程序相比C/C++ 程序需要额外的时间
    private static final long JAVA_PROGRAM_TIME_COST = 1000L;
    // Java 程序相比C/C++ 程序需要额外的内存
    private static final long JAVA_PROGRAM_MEMORY_COST = 10 * 1024 * 1024L;

    @Override
    protected Long getAdjustedMemoryLimit(Long memoryLimit) {
        return memoryLimit + JAVA_PROGRAM_MEMORY_COST;
    }

    @Override
    protected Long getAdjustedTimeLimit(Long timeLimit) {
        return timeLimit + JAVA_PROGRAM_TIME_COST;
    }
}