package com.cjf.thread.expand.task.listener;

import com.cjf.thread.expand.task.ThreadTaskRun;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * @ProjectName: Thread
 * @Package: com.cjf.thread.expand.app.listener
 * @ClassName: IThreadTask
 * @Description: java类作用描述
 * @Author: 蔡俊峰
 * @CreateDate: 2019/11/20 19:58
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/11/20 19:58
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface IThreadTask {
    /**
     * 添加监听事件
     *
     * @param executorServiceListener 监听事件
     */
    IThreadTask addExecutorServiceListener(@NonNull final ThreadTaskListener executorServiceListener);

    /**
     * 移除监听事件
     *
     * @param executorServiceListener 监听事件
     */
    IThreadTask removeExecutorServiceListener(@NonNull final ThreadTaskListener executorServiceListener);

    /**
     * 清空所有
     */
    void clearAll();

    /**
     * 立刻中断
     */
    void shutDownNow();

    /**
     * 线程并行执行
     *
     * @param executorServiceRunnableList 内容
     */
    <T> void submitAll(@NonNull final List<ThreadTaskRun<T>> executorServiceRunnableList);

    /**
     * 线程并行执行
     *
     * @param executorServiceRunnableList 内容
     */
    default <T> void submitAll(@NonNull final ThreadTaskRun<T>... executorServiceRunnableList) {
        submitAll(Arrays.asList(executorServiceRunnableList));
    }

    /**
     * 线程执行
     *
     * @param executorServiceRunnableList 要执行的
     * @param isParallel                  true 并行线程 false 串行线程
     */
    <T> void submitAll(@NonNull final List<ThreadTaskRun<T>> executorServiceRunnableList, boolean isParallel);

}
