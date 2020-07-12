package com.cjf.thread.expand.executor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.ExecutorService;

public final class ThreadTaskExecutor implements ThreadExecutor {

    private static volatile ThreadTaskExecutor sInstance;

    @NonNull
    private ThreadExecutor mDelegate;

    @NonNull
    private final ThreadExecutor mDefaultTaskExecutor;


    private ThreadTaskExecutor() {
        mDefaultTaskExecutor = new DefaultThreadExecutor();
        mDelegate = mDefaultTaskExecutor;
    }

    /**
     * Returns an instance of the task executor.
     *
     * @return The singleton ThreadTaskExecutor.
     */
    @NonNull
    public static ThreadTaskExecutor getInstance() {
        if (sInstance != null) {
            return sInstance;
        }
        synchronized (ThreadTaskExecutor.class) {
            if (sInstance == null) {
                sInstance = new ThreadTaskExecutor();
            }
        }
        return sInstance;
    }

    /**
     * Sets a delegate to handle task execution requests.
     * <p>
     * If you have a common executor, you can set it as the delegate and App Toolkit components will
     * use your executors. You may also want to use this for your tests.
     * <p>
     * Calling this method with {@code null} sets it to the default ThreadExecutor.
     *
     * @param taskExecutor The task executor to handle task requests.
     */
    public void setDelegate(@Nullable ThreadExecutor taskExecutor) {
        mDelegate = taskExecutor == null ? mDefaultTaskExecutor : taskExecutor;
    }

    @Override
    @NonNull
    public ExecutorService getExecutorService() {
        return mDelegate.getExecutorService();
    }

    @Override
    @NonNull
    public ExecutorService getIO() {
        return mDelegate.getIO();
    }

    @Override
    @NonNull
    public ExecutorService singleIO() {
        return mDelegate.singleIO();
    }

    @Override
    public boolean postDelayed(@NonNull Runnable runnable, long delayMillis) {
        return mDelegate.postDelayed(runnable, delayMillis);
    }

    @Override
    public boolean postAtTime(@NonNull Runnable runnable, long uptimeMillis) {
        return mDelegate.postAtTime(runnable, uptimeMillis);
    }

    @Override
    public void postToMain(@NonNull Runnable runnable) {
        mDelegate.postToMain(runnable);
    }

    @Override
    public boolean isMainThread() {
        return mDelegate.isMainThread();
    }

    @Override
    public void postIo(@NonNull Runnable runnable) {
        mDelegate.postIo(runnable);
    }
}
