/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cjf.thread.expand;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.cjf.thread.expand.appexecutor.IThreadApp;
import com.cjf.thread.expand.appexecutor.ThreadMain;

import java.lang.ref.SoftReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;

/**
 * <pre>
 *     desc   : 应用的全局线程池 （包括单线程池的磁盘io，多线程池的网络io和主线程）
 *     author : xuexiang
 *     time   : 2018/4/20 下午4:52
 * </pre>
 */
final class ThreadApp implements IThreadApp {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    // We want at least 2 threads and at most 4 threads in the core pool,
    // preferring to have 1 less than the CPU count to avoid saturating
    // the CPU with background work
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 30;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    };

    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE);

    private static ThreadApp sInstance;

    /**
     * 单线程池
     */
    private SoftReference<ExecutorService> mSingleIO;

    /**
     * 多线程池
     */
    private SoftReference<ExecutorService> mPoolIO;

    /**
     * 主线程
     */
    private ThreadMain mMainThread;

    private ThreadApp(ExecutorService singleIO, ThreadMain mainThread) {
        mSingleIO = new SoftReference<>(singleIO);
        mMainThread = mainThread;
    }

    /**
     * 更新多线程池
     *
     * @param nThreads 线程池线程的数量
     * @return
     */
    @Override
    public IThreadApp updatePoolIO(int nThreads) {
        mPoolIO = new SoftReference<>(Executors.newFixedThreadPool(nThreads));
        return this;
    }

    private ThreadApp() {
        this(Executors.newSingleThreadExecutor(),
                new MainThreadExecutor());
    }

    /**
     * 获取线程管理实例
     *
     * @return
     */
    public static IThreadApp get() {
        if (sInstance == null) {
            synchronized (ThreadApp.class) {
                if (sInstance == null) {
                    sInstance = new ThreadApp();
                }
            }
        }
        return sInstance;
    }

    @Override
    public ExecutorService singleIO() {
        if (mSingleIO == null || mSingleIO.get() == null) {
            mSingleIO = new SoftReference<>(Executors.newSingleThreadExecutor());
        }
        return mSingleIO.get();
    }
    @Override
    public ExecutorService poolIO() {
        if (mPoolIO == null || mPoolIO.get() == null) {
            mPoolIO = new SoftReference<>(getExecutorService());
        }
        return mPoolIO.get();
    }
    @Override
    public ThreadMain mainThread() {
        if (mMainThread == null) {
            mMainThread = new MainThreadExecutor();
        }
        return mMainThread;
    }

    /**
     * 获取并行的线程池
     *
     * @return
     */
    @Override
    public ExecutorService getExecutorService() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                sPoolWorkQueue, sThreadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }

    @Override
    public boolean postDelayed(Runnable r, long delayMillis) {
        return mainThread().postDelayed(r, delayMillis);
    }

    @Override
    public boolean postAtTime(Runnable r, long uptimeMillis) {
        return mainThread().postAtTime(r, uptimeMillis);
    }

    @Override
    public Message getMessage() {
        return mainThread().getMessage();
    }

    @Override
    public void execute(@NonNull Runnable command) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            // 主线程
            command.run();
        } else {
            // 子线程切换到主线程
            mainThread().execute(command);
        }
    }


    private static class MainThreadExecutor implements ThreadMain {

        private Handler mainThreadHandler;

        /**
         * 懒加载
         *
         * @return 主线程Handler
         */
        private Handler getMainThreadHandler() {
            if (mainThreadHandler == null) {
                mainThreadHandler = new Handler(Looper.getMainLooper());
            }
            return mainThreadHandler;
        }

        @Override
        public void execute(@NonNull Runnable command) {
            getMainThreadHandler().post(command);
        }

        @Override
        public boolean postDelayed(Runnable r, long delayMillis) {
            return getMainThreadHandler().postDelayed(r, delayMillis);
        }

        @Override
        public boolean postAtTime(Runnable r, long uptimeMillis) {
            return getMainThreadHandler().postAtTime(r, uptimeMillis);
        }

        @Override
        public Message getMessage() {
            return mainThreadHandler.obtainMessage();
        }
    }
}
