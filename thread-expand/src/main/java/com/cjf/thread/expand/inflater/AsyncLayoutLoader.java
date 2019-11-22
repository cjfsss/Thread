package com.cjf.thread.expand.inflater;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.concurrent.CountDownLatch;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.collection.SparseArrayCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * 调用入口类；同时解决加载和获取View在不同类的场景
 */
public final class AsyncLayoutLoader implements LifecycleObserver {

    private int mLayoutId;
    private View mRealView;
    private Context mContext;
    private ViewGroup mRootView;
    private CountDownLatch mCountDownLatch;
    private AsyncLayoutInflaterPlus mInflater;
    private static SparseArrayCompat<AsyncLayoutLoader> sArrayCompat = new SparseArrayCompat<AsyncLayoutLoader>();

    public static AsyncLayoutLoader getInstance(Context context) {
        return new AsyncLayoutLoader(context);
    }

    private AsyncLayoutLoader(Context context) {
        if (context instanceof LifecycleOwner) {
            ((LifecycleOwner) context).getLifecycle().addObserver(this);
        }
        this.mContext = context;
        mCountDownLatch = new CountDownLatch(1);
    }

    @UiThread
    public void inflate(@LayoutRes int resId, @Nullable ViewGroup parent) {
        inflate(resId, parent, null);
    }

    @UiThread
    public void inflate(@LayoutRes int resId,@NonNull AsyncLayoutInflaterPlus.OnInflateFinishedListener listener) {
        inflate(resId, null, listener);
    }

    @UiThread
    public void inflate(@LayoutRes int resId, @Nullable ViewGroup parent,
                        AsyncLayoutInflaterPlus.OnInflateFinishedListener listener) {
        mRootView = parent;
        mLayoutId = resId;
        sArrayCompat.append(mLayoutId, this);
        if (listener == null) {
            listener = new AsyncLayoutInflaterPlus.OnInflateFinishedListener() {
                @Override
                public void onInflateFinished(View view, int resid, ViewGroup parent) {
                    mRealView = view;
                }
            };
        }
        mInflater = new AsyncLayoutInflaterPlus(mContext);
        mInflater.inflate(resId, parent, mCountDownLatch, listener);
    }

    /**
     * getLayoutLoader 和 getRealView 方法配对出现
     * 用于加载和获取View在不同类的场景
     *
     * @param resid
     * @return
     */
    public static AsyncLayoutLoader getLayoutLoader(int resid) {
        return sArrayCompat.get(resid);
    }

    /**
     * getLayoutLoader 和 getRealView 方法配对出现
     * 用于加载和获取View在不同类的场景
     *
     * @return
     */
    public View getRealView() {
        if (mRealView == null && !mInflater.isRunning()) {
            mInflater.cancel();
            inflateSync();
        } else if (mRealView == null) {
            try {
                mCountDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setLayoutParamByParent(mContext, mRootView, mLayoutId, mRealView);
        } else {
            setLayoutParamByParent(mContext, mRootView, mLayoutId, mRealView);
        }
        return mRealView;
    }


    /**
     * 根据Parent设置异步加载View的LayoutParamsView
     *
     * @param context
     * @param parent
     * @param layoutResId
     * @param view
     */
    private static void setLayoutParamByParent(Context context, ViewGroup parent, int layoutResId, View view) {
        if (parent == null) {
            return;
        }
        final XmlResourceParser parser = context.getResources().getLayout(layoutResId);
        try {
            final AttributeSet attrs = Xml.asAttributeSet(parser);
            ViewGroup.LayoutParams params = parent.generateLayoutParams(attrs);
            view.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            parser.close();
        }
    }

    private void inflateSync() {
        mRealView = LayoutInflater.from(mContext).inflate(mLayoutId, mRootView, false);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy(LifecycleOwner owner) {
        mRealView = null;
        mContext = null;
        mRootView = null;
        owner.getLifecycle().removeObserver(this);
    }
}