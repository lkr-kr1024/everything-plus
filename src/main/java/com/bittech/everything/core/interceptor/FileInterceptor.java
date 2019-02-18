package com.bittech.everything.core.interceptor;

import java.io.File;

@FunctionalInterface
//函数式接口，后续想写lamda表达式
public interface FileInterceptor {
    void apply(File file);
}
