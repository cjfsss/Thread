package com.cjf.thread.expand.task.listener;


import com.cjf.thread.expand.task.ThreadProgress;

/**
 * @ProjectName: MobileEnforcement-BeiJing
 * @Package: com.mapuni.android.core.net.thread.listener
 * @ClassName: ThreadTaskListener
 * @Description: java类作用描述
 * @Author: 蔡俊峰
 * @CreateDate: 2019-7-30 11:41
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019-7-30 11:41
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface ThreadProgressListener {

    void onProgressChange(ThreadProgress progress);

    /**
     * 线程结束，始终都会调用
     *
     * @param progress
     */
    void onThreadFinish(ThreadProgress progress);


}
