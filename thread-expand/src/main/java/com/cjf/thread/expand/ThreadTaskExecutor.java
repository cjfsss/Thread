/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cjf.thread.expand;

import com.cjf.thread.expand.executor.DefaultThreadExecutor;
import com.cjf.thread.expand.executor.ThreadExecutor;

import java.util.concurrent.ExecutorService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A static class that serves as a central point to execute common tasks.
 * <p>
 *
 * @hide This API is not final.
 */
class ThreadTaskExecutor implements ThreadExecutor {
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

    @NonNull
    @Override
    public ExecutorService getExecutorService() {
        return mDelegate.getExecutorService();
    }

    @NonNull
    @Override
    public ExecutorService getIO() {
        return mDelegate.getIO();
    }

    @NonNull
    @Override
    public ExecutorService singleIO() {
        return mDelegate.singleIO();
    }

    @Override
    public boolean postDelayed(Runnable runnable, long delayMillis) {
        return mDelegate.postDelayed(runnable, delayMillis);
    }

    @Override
    public boolean postAtTime(Runnable runnable, long uptimeMillis) {
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
