package com.cjf.thread.expand.task.listener;

/**
 * @Packname: com.mapuni.arcgis.http.upload.listener
 * @ClassName: ThreadResultListener
 * @Version: 1.0
 * @Author: CaiJunFeng on 2018-10-17 19:12
 * @Description:
 */
public interface ThreadResultListener {

    void onSuccess();//所有线程执行完毕

    void onFailed();//所有线程执行出现问题
}
