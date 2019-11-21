package com.cjf.thread.expand.appexecutor;

import android.os.Message;

import java.util.concurrent.Executor;

import androidx.annotation.NonNull;

/**
 * @ProjectName: XAOP-master
 * @Package: com.xuexiang.xaop.checker
 * @ClassName: ThreadMain
 * @Description: 主线程接口
 * @Author: 蔡俊峰
 * @CreateDate: 2019-5-29 18:01
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019-5-29 18:01
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface ThreadMain extends Executor {
    /**
     * 延迟在主线程执行
     *
     * @param r           运行
     * @param delayMillis 延迟时间
     * @return
     */
    boolean postDelayed( final Runnable r, final long delayMillis);

    boolean postAtTime(Runnable r, long uptimeMillis);

    @NonNull
    Message getMessage();
}
