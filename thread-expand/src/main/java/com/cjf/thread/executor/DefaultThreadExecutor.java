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

package com.cjf.thread.executor;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

final class DefaultThreadExecutor implements ThreadExecutor {

    private static final String TAG = "DefaultThreadExecutor";

    private final Object mLock = new Object();

    /**
     * 多线程池
     */
    @Nullable
    private SoftReference<ExecutorService> mPoolIO;

    @Nullable
    private SoftReference<ExecutorService> mSingleIO;

    private volatile Handler mMainHandler;

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    // We want at least 2 threads and at most 4 threads in the core pool,
    // preferring to have 1 less than the CPU count to avoid saturating
    // the CPU with background work
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 30;

    @NonNull
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "AsyncTask #" + mCount.getAndIncrement());
        }
    };

    @NonNull
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<>(Integer.MAX_VALUE);

    @NonNull
    public final ExecutorService getExecutorService() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                                                                       KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                                                                       sPoolWorkQueue, sThreadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }

    @Override
    @NonNull
    public ExecutorService getIO() {
        if (mPoolIO == null || mPoolIO.get() == null) {
            mPoolIO = new SoftReference<>(getExecutorService());
        }
        return mPoolIO.get();
    }

    @Override
    @NonNull
    public ExecutorService singleIO() {
        if (mSingleIO == null || mSingleIO.get() == null) {
            mSingleIO = new SoftReference<>(Executors.newSingleThreadExecutor());
        }
        return mSingleIO.get();
    }

    @SuppressWarnings("FinalPrivateMethod")
    @NonNull
    private final Handler getMainThread() {
        if (mMainHandler == null) {
            synchronized (mLock) {
                if (mMainHandler == null) {
                    mMainHandler = createAsync(Looper.getMainLooper());
                }
            }
        }
        return mMainHandler;
    }

    @Override
    public boolean postDelayed(@NonNull final Runnable runnable, final long delayMillis) {
        return getMainThread().postDelayed(runnable, delayMillis);
    }

    @Override
    public boolean postAtTime(@NonNull final Runnable runnable, final long uptimeMillis) {
        return getMainThread().postAtTime(runnable, uptimeMillis);
    }

    @Override
    public void postToMain(@NonNull final Runnable runnable) {
        getMainThread().post(runnable);
    }

    @Override
    public boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    @Override
    public void postIo(@NonNull final Runnable runnable) {
        getIO().execute(runnable);
    }

    @SuppressWarnings("JavaReflectionMemberAccess")
    @NonNull
    private static Handler createAsync(@NonNull Looper looper) {
        if (Build.VERSION.SDK_INT >= 28) {
            return Handler.createAsync(looper);
        }
        try {
            return Handler.class.getDeclaredConstructor(Looper.class, Handler.Callback.class, boolean.class)
                                .newInstance(looper, null, true);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "getMainThread createAsync" + e.getMessage());
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "getMainThread createAsync" + e.getMessage());
        } catch (InstantiationException e) {
            Log.e(TAG, "getMainThread createAsync" + e.getMessage());
        } catch (InvocationTargetException e) {
            return new Handler(looper);
        }
        return new Handler(looper);
    }
}
