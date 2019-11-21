package com.cjf.thread.expand;

import android.util.Log;

import com.cjf.thread.expand.task.ThreadProgress;
import com.cjf.thread.expand.task.ThreadResult;
import com.cjf.thread.expand.task.ThreadTaskRun;
import com.cjf.thread.expand.task.listener.IThreadTask;
import com.cjf.thread.expand.task.listener.ThreadProgressListener;
import com.cjf.thread.expand.task.listener.ThreadResultListener;
import com.cjf.thread.expand.task.listener.ThreadTaskListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * @ProjectName: MobileEnforcement-BeiJing
 * @Package: com.mapuni.android.core.net.thread
 * @ClassName: ThreadTaskRun
 * @Description: java类作用描述
 * @Author: 蔡俊峰
 * @CreateDate: 2019-7-29 18:38
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019-7-29 18:38
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
final class ThreadTask implements LifecycleObserver, IThreadTask {

    private ExecutorService mExecutorService;//线程池
    private CountDownLatch mCountDownLatch;//计数器

    private List<ThreadTaskListener> mExecutorServiceListeners;
    /**
     * 某个线程失败
     */
    private boolean mErrorThread = false;

    ThreadTask() {
    }

    ThreadTask(@NonNull final LifecycleOwner lifecycleOwner) {
        lifecycleOwner.getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy(LifecycleOwner owner) {
        // this owner is your activity instance
        owner.getLifecycle().removeObserver(this);
        clearAll();
    }

    @Override
    @NonNull
    public IThreadTask addExecutorServiceListener(@NonNull final ThreadTaskListener executorServiceListener) {
        if (mExecutorServiceListeners == null) {
            mExecutorServiceListeners = new ArrayList<>();
        }
        if (!mExecutorServiceListeners.contains(executorServiceListener)) {
            mExecutorServiceListeners.add(executorServiceListener);
        }
        return this;
    }

    @Override
    @NonNull
    public IThreadTask removeExecutorServiceListener(@NonNull final ThreadTaskListener executorServiceListener) {
        if (mExecutorServiceListeners != null && !mExecutorServiceListeners.isEmpty()) {
            mExecutorServiceListeners.remove(executorServiceListener);
        }
        return this;
    }

    @Override
    public void clearAll() {
        if (mExecutorServiceListeners != null) {
            mExecutorServiceListeners.clear();
        }
    }

    /**
     * 立刻中断线程
     */
    @Override
    public void shutDownNow() {
        if (mExecutorService == null) {
            return;
        }
        mExecutorService.shutdownNow();//中断所有线程的执行
    }

    /**
     * 线程并行执行
     *
     * @param executorServiceRunnableList 要执行的
     */
    @Override
    public <T> void submitAll(@NonNull final List<ThreadTaskRun<T>> executorServiceRunnableList) {
        submitAll(executorServiceRunnableList, true);
    }

    /**
     * 线程执行
     *
     * @param executorServiceRunnableList 要执行的
     * @param isParallel                  true 并行线程 false 串行线程
     */
    @Override
    public <T> void submitAll(@NonNull final List<ThreadTaskRun<T>> executorServiceRunnableList,final boolean isParallel) {
        int totalCount = executorServiceRunnableList.size();

        mCountDownLatch = new CountDownLatch(totalCount);
        if (isParallel) {
            // 获取并行线程
            mExecutorService = ConnectThread.getAppExecutor().getExecutorService();
        } else {
            // 串行线程
            mExecutorService = ConnectThread.getAppExecutor().singleIO();
        }
        mExecutorService.submit(new ThreadResult(mCountDownLatch, new ThreadResultListener() {//创建一个监听线程
            @Override
            public void onSuccess() {
                if (mErrorThread) {
                    Log.w(ConnectThread.EXECUTOR_TASK, "ALL_FAILED");
                    if (mExecutorServiceListeners != null) {
                        for (ThreadTaskListener listener : mExecutorServiceListeners) {
                            listener.onAllFailed();
                        }
                    }
                    return;
                }
                Log.w(ConnectThread.EXECUTOR_TASK, "ALL_SUCCESS");
                if (mExecutorServiceListeners != null) {
                    for (ThreadTaskListener listener : mExecutorServiceListeners) {
                        listener.onAllSuccess();
                    }
                }
            }

            @Override
            public void onFailed() {
                Log.w(ConnectThread.EXECUTOR_TASK, "ALL_FAILED");
                if (mExecutorServiceListeners != null) {
                    for (ThreadTaskListener listener : mExecutorServiceListeners) {
                        listener.onAllFailed();
                    }
                }
            }
        }));
        for (int i = 0; i < totalCount; i++) {//模拟生成任务线程
            ThreadProgress progress = new ThreadProgress();
            progress.threadTotalCount = totalCount;
            progress.position = i;
            ThreadTaskRun serviceRunnable = executorServiceRunnableList.get(i);
            serviceRunnable.setDownLatch(mCountDownLatch);
            serviceRunnable.setExecutorServiceProgress(progress);
            serviceRunnable.setExecutorServiceThreadListener(getExecutorServiceThreadListener());
            mExecutorService.submit(serviceRunnable);
        }
        mExecutorService.shutdown();//关闭线程池
    }

    @NonNull
    private ThreadProgressListener getExecutorServiceThreadListener() {
        return new ThreadProgressListener() {
            @Override
            public void onProgressChange(@NonNull final ThreadProgress progress) {
                Log.w(ConnectThread.EXECUTOR_TASK, "THREAD_PROGRESS" + progress.toString());
                if (mExecutorServiceListeners != null) {
                    for (ThreadTaskListener listener : mExecutorServiceListeners) {
                        listener.onProgressChange(progress);
                    }
                }
            }

            @Override
            public void onThreadFinish(@NonNull final ThreadProgress progress) {
                if (!progress.isCurrentSuccess) {
                    // 有失败的线程
                    mErrorThread = true;
                }
                Log.w(ConnectThread.EXECUTOR_TASK, "THREAD_FINISH" + progress.toString());
                if (mExecutorServiceListeners != null) {
                    for (ThreadTaskListener listener : mExecutorServiceListeners) {
                        listener.onThreadFinish(progress);
                    }
                }
            }
        };
    }

}
