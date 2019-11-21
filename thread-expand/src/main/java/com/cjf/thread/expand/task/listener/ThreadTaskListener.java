package com.cjf.thread.expand.task.listener;

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
public interface ThreadTaskListener extends ThreadProgressListener {

    /**
     * 所有线程结束，成功
     */
    default void onAllSuccess(){

    }

    /**
     * 所有线程结束，失败
     */
    default void onAllFailed(){

    }
}
