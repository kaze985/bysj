package com.lppnb.bysj.judgeservice.judge;

import com.lppnb.bysj.codesandbox.JudgeInfo;
import com.lppnb.bysj.entity.QuestionSubmit;
import com.lppnb.bysj.enums.QuestionSubmitLanguageEnum;
import com.lppnb.bysj.judgeservice.judge.strategy.DefaultJudgeStrategy;
import com.lppnb.bysj.judgeservice.judge.strategy.JavaLanguageJudgeStrategy;
import com.lppnb.bysj.judgeservice.judge.strategy.JudgeContext;
import com.lppnb.bysj.judgeservice.judge.strategy.JudgeStrategy;
import org.springframework.stereotype.Service;

/**
 * 判题管理器（切换判题策略）
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if (QuestionSubmitLanguageEnum.JAVA.getValue().equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
