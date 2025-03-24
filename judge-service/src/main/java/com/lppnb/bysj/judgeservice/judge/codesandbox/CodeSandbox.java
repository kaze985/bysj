package com.lppnb.bysj.judgeservice.judge.codesandbox;

import com.lppnb.bysj.codesandbox.ExecuteCodeRequest;
import com.lppnb.bysj.codesandbox.ExecuteCodeResponse;

/**
 * 代码沙箱接口定义
 */
public interface CodeSandbox {

    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
