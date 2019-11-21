package com.cjf.thread.expand.appexecutor;

import java.util.concurrent.ExecutorService;

import androidx.annotation.NonNull;

/**
 * @ProjectName: Thread
 * @Package: com.cjf.thread.expand.app.listener
 * @ClassName: IThreadApp
 * @Description: java类作用描述
 * @Author: 蔡俊峰
 * @CreateDate: 2019/11/20 20:12
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/11/20 20:12
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface IThreadApp extends ThreadMain {
    /**
     * 更新线程池的线程数
     *
     * @param threadSize 线程数
     * @return 当前对象
     */
    IThreadApp updatePoolIO(final int threadSize);

    /**
     * 单线程线程池
     */
    @NonNull
    ExecutorService singleIO();

    /**
     * 多线程线程池
     */
    @NonNull
    ExecutorService poolIO();

    /**
     * 主线程
     *
     * @return 主线程
     */
    @NonNull
    ThreadMain mainThread();

    /**
     * 获取并行的线程池
     *
     * @return 并行的线程池
     */
    @NonNull
    ExecutorService getExecutorService();
}
