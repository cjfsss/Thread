package com.cjf.thread.expand.inflater;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.util.Pools;
import androidx.core.view.LayoutInflaterCompat;

/**
 * 实现异步加载布局的功能，修改点：
 * 1. 单一线程；
 * 2. super.onCreate之前调用没有了默认的Factory；
 * 3. 排队过多的优化；
 */
public final class AsyncLayoutInflaterPlus {

    private static final String TAG = "AsyncLayoutInflaterPlus";
    private final Handler mHandler;
    private final LayoutInflater mInflater;
    private InflateRunnable mInflateRunnable;
    // 真正执行加载任务的线程池
    private final static ExecutorService sExecutor = Executors.newFixedThreadPool(Math.max(2,
            Math.min(Runtime.getRuntime().availableProcessors() - 1, 4)));
    // InflateRequest pool
    private final static Pools.SynchronizedPool<AsyncLayoutInflaterPlus.InflateRequest> sRequestPool = new Pools.SynchronizedPool<>(10);
    private Future<?> future;

    public AsyncLayoutInflaterPlus(@NonNull Context context) {
        mInflater = new AsyncLayoutInflaterPlus.BasicInflater(context);
        mHandler = new Handler(mHandlerCallback);
    }

    @UiThread
    public void inflate(@LayoutRes int resid, @Nullable ViewGroup parent, @NonNull CountDownLatch countDownLatch,
                        @NonNull OnInflateFinishedListener callback) {
        if (callback == null) {
            throw new NullPointerException("callback argument may not be null!");
        }
        AsyncLayoutInflaterPlus.InflateRequest request = obtainRequest();
        request.inflater = this;
        request.resid = resid;
        request.parent = parent;
        request.callback = callback;
        request.countDownLatch = countDownLatch;
        mInflateRunnable = new InflateRunnable(request);
        future = sExecutor.submit(mInflateRunnable);
    }

    public void cancel() {
        future.cancel(true);
    }

    /**
     * 判断这个任务是否已经开始执行
     *
     * @return
     */
    public boolean isRunning() {
        return mInflateRunnable.isRunning();
    }

    private final Handler.Callback mHandlerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            AsyncLayoutInflaterPlus.InflateRequest request = (AsyncLayoutInflaterPlus.InflateRequest) msg.obj;
            if (request.view == null) {
                request.view = mInflater.inflate(
                        request.resid, request.parent, false);
            }
            request.callback.onInflateFinished(
                    request.view, request.resid, request.parent);
            request.countDownLatch.countDown();
            releaseRequest(request);
            return true;
        }
    };

    private class InflateRunnable implements Runnable {
        private InflateRequest request;
        private boolean isRunning;

        public InflateRunnable(InflateRequest request) {
            this.request = request;
        }

        @Override
        public void run() {
            isRunning = true;
            try {
                request.view = request.inflater.mInflater.inflate(
                        request.resid, request.parent, false);
            } catch (RuntimeException ex) {
                // Probably a Looper failure, retry on the UI thread
                Log.w(TAG, "Failed to inflate resource in the background! Retrying on the UI"
                        + " thread", ex);
            }
            Message.obtain(request.inflater.mHandler, 0, request)
                    .sendToTarget();
        }

        public boolean isRunning() {
            return isRunning;
        }
    }

    private static class InflateRequest {
        AsyncLayoutInflaterPlus inflater;
        ViewGroup parent;
        int resid;
        View view;
        OnInflateFinishedListener callback;
        CountDownLatch countDownLatch;

        InflateRequest() {
        }
    }

    private static class BasicInflater extends LayoutInflater {
        private static final String[] sClassPrefixList = {
                "android.widget.",
                "android.webkit.",
                "android.app."
        };

        BasicInflater(Context context) {
            super(context);
            if (context instanceof AppCompatActivity) {
                // 加上这些可以保证AppCompatActivity的情况下，super.onCreate之前
                // 使用AsyncLayoutInflater加载的布局也拥有默认的效果
                AppCompatDelegate appCompatDelegate = ((AppCompatActivity) context).getDelegate();
                if (appCompatDelegate instanceof Factory2) {
                    LayoutInflaterCompat.setFactory2(this, (Factory2) appCompatDelegate);
                }
            }
        }

        @Override
        public LayoutInflater cloneInContext(Context newContext) {
            return new AsyncLayoutInflaterPlus.BasicInflater(newContext);
        }

        @Override
        protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
            for (String prefix : sClassPrefixList) {
                try {
                    View view = createView(name, prefix, attrs);
                    if (view != null) {
                        return view;
                    }
                } catch (ClassNotFoundException e) {
                    // In this case we want to let the base class take a crack
                    // at it.
                }
            }

            return super.onCreateView(name, attrs);
        }
    }

    public AsyncLayoutInflaterPlus.InflateRequest obtainRequest() {
        AsyncLayoutInflaterPlus.InflateRequest obj = sRequestPool.acquire();
        if (obj == null) {
            obj = new AsyncLayoutInflaterPlus.InflateRequest();
        }
        return obj;
    }

    public void releaseRequest(AsyncLayoutInflaterPlus.InflateRequest obj) {
        obj.callback = null;
        obj.inflater = null;
        obj.parent = null;
        obj.resid = 0;
        obj.view = null;
        sRequestPool.release(obj);
    }

}
