package com.cjf.thread.expand.inflater;

import android.view.View;
import android.view.ViewGroup;

/**
 * @ProjectName: Thread
 * @Package: com.cjf.thread.expand.inflater
 * @ClassName: OnInflateFinishedListener
 * @Description: java类作用描述
 * @Author: 蔡俊峰
 * @CreateDate: 2019/11/22 13:52
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/11/22 13:52
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface OnInflateFinishedListener {

    void onInflateFinished(View view, int resId, ViewGroup parent);
}
