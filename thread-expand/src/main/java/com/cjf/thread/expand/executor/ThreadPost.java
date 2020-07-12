package com.cjf.thread.expand.executor;

import androidx.annotation.NonNull;

/**
 * @ProjectName: Thread
 * @Package: com.cjf.thread.expand.executor
 * @ClassName: ThreadPost
 * @Description: java类作用描述
 * @Author: 蔡俊峰
 * @CreateDate: 2019/11/22 11:26
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/11/22 11:26
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface ThreadPost {

    /**
     * 延迟在主线程执行
     *
     * @param runnable    运行
     * @param delayMillis 延迟时间
     */
    boolean postDelayed(@NonNull final Runnable runnable, final long delayMillis);

    /**
     * 指定时间执行
     *
     * @param runnable     主线程
     * @param uptimeMillis 设定时间
     */
    boolean postAtTime(@NonNull final Runnable runnable, final long uptimeMillis);

    /**
     * 切换到主线程
     *
     * @param runnable 主线程
     */
    void postToMain(@NonNull final Runnable runnable);

    /**
     * 在主线程上运行
     *
     * @param runnable 主线程
     */
    default void postOnMain(@NonNull final Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            postToMain(runnable);
        }
    }

    /**
     * 是否在主线程
     *
     * @return true 在主线程
     */
    boolean isMainThread();

    /**
     * 运行在工作线程
     *
     * @param runnable 工作线程
     */
    void postIo(@NonNull final Runnable runnable);
}
