package com.lppnb.bysj.judgeservice.judge.codesandbox.impl;

import com.lppnb.bysj.codesandbox.ExecuteCodeRequest;
import com.lppnb.bysj.codesandbox.ExecuteCodeResponse;
import com.lppnb.bysj.judgeservice.judge.codesandbox.CodeSandbox;

/**
 * 第三方代码沙箱（调用网上现成的代码沙箱）
 */
public class ThirdPartyCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
