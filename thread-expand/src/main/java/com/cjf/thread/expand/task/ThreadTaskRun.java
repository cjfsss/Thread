package com.cjf.thread.expand.task;


import android.util.Log;

import com.cjf.thread.expand.ConnectThread;
import com.cjf.thread.expand.task.listener.ThreadProgressListener;

import java.util.concurrent.CountDownLatch;

/**
 * @ProjectName: MobileEnforcement-BeiJing
 * @Package: com.mapuni.android.core.net.thread
 * @ClassName: ThreadTaskRun
 * @Description: java类作用描述
 * @Author: 蔡俊峰
 * @CreateDate: 2019-7-30 13:41
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019-7-30 13:41
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public abstract class ThreadTaskRun<T> implements Runnable, ThreadProgressListener {

    private CountDownLatch downLatch;//计数器
    private T data;//文件名
    private ThreadProgressListener mExecutorServiceThreadListener;//任务线程回调接口
    private ThreadProgress mExecutorServiceProgress;

    public ThreadTaskRun(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setDownLatch(CountDownLatch downLatch) {
        this.downLatch = downLatch;
    }

    public void setExecutorServiceThreadListener(ThreadProgressListener executorServiceThreadListener) {
        mExecutorServiceThreadListener = executorServiceThreadListener;
    }

    public void setExecutorServiceProgress(ThreadProgress executorServiceProgress) {
        mExecutorServiceProgress = executorServiceProgress;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void run() {
        ThreadProgress progress = mExecutorServiceProgress.copy();
        try {
            // 多线程上传或者下载文件
            execute(progress, data);
            progress.isCurrentSuccess = true;
        } catch (Throwable throwable) {
            Log.e(ConnectThread.EXECUTOR_TASK, throwable.getMessage());
            progress.throwable = throwable;
            progress.isCurrentSuccess = false;
        } finally {
            this.downLatch.countDown();
            progress.threadCurrentCount = (int) (progress.threadTotalCount - downLatch.getCount());
            progress.threadCurrentProgress = progress.threadCurrentCount * 100 / progress.threadTotalCount;
            onThreadFinish(progress);
        }
    }

    /**
     * 执行耗时操作
     *
     * @param progress 进度
     * @param data     数据
     */
    public abstract void execute(ThreadProgress progress, T data) throws Throwable;

    public void onProgressChange(long currentCount, long totalCount) {
        ThreadProgress progress = mExecutorServiceProgress.copy();
        progress.currentCount = currentCount;
        progress.totalCount = totalCount;
        progress.currentProgress = (int) (currentCount * 100 / totalCount);
        onProgressChange(progress);
    }

    @Override
    public void onProgressChange(ThreadProgress progress) {
        ConnectThread.getAppExecutor().execute(() -> {
            if (mExecutorServiceThreadListener != null) {
                mExecutorServiceThreadListener.onProgressChange(progress);
            }
        });
    }

    @Override
    public void onThreadFinish(ThreadProgress progress) {
        ConnectThread.getAppExecutor().execute(() -> {
            if (mExecutorServiceThreadListener != null) {
                mExecutorServiceThreadListener.onThreadFinish(progress);
            }
        });
    }
}