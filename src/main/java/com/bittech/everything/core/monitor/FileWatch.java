package com.bittech.everything.core.monitor;

import com.bittech.everything.core.common.HandlePath;

//shipin8/11:40
public interface FileWatch {
    /**
     * 监听启动
     */
    void start();

    /**
     * 监听的目录
     */
    void monitor(HandlePath handlePath);

    /**
     * 监听停止
     */
    void stop();
}
