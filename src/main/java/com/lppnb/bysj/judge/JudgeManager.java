package com.lppnb.bysj.judge;

import com.lppnb.bysj.judge.codesandbox.model.JudgeInfo;
import com.lppnb.bysj.judge.strategy.DefaultJudgeStrategy;
import com.lppnb.bysj.judge.strategy.JavaLanguageJudgeStrategy;
import com.lppnb.bysj.judge.strategy.JudgeContext;
import com.lppnb.bysj.judge.strategy.JudgeStrategy;
import com.lppnb.bysj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 * 切换判题策略
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
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
