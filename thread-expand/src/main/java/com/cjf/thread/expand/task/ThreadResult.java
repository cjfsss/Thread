package com.cjf.thread.expand.task;


import com.cjf.thread.expand.ConnectThread;
import com.cjf.thread.expand.task.listener.ThreadResultListener;

import java.util.concurrent.CountDownLatch;

import androidx.annotation.NonNull;

/**
 * @Packname: com.mapuni.arcgis.http.upload
 * @ClassName: UploadListener
 * @Version: 1.0
 * @Author: CaiJunFeng on 2018-10-17 19:13
 * @Description:
 */
public class ThreadResult implements Runnable {

    private final CountDownLatch downLatch;
    private final ThreadResultListener listener;

    public ThreadResult(@NonNull final CountDownLatch countDownLatch,@NonNull final ThreadResultListener listener) {
        this.downLatch = countDownLatch;
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            downLatch.await();
            onSuccess();//顺利完成
        } catch (Exception e) {
            onFailed();
        }
    }

    private void onSuccess() {
        //所有线程执行完毕
        ConnectThread.getExecutor().execute(listener::onSuccess);
    }

    private void onFailed() {
        //所有线程执行出现问题
        ConnectThread.getExecutor().execute(listener::onFailed);
    }
}
