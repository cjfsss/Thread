package com.cjf.thread.expand;

import com.cjf.thread.expand.task.listener.IThreadTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

/**
 * @ProjectName: Thread
 * @Package: com.cjf.thread.expand
 * @ClassName: ConnectThread
 * @Description: java类作用描述
 * @Author: 蔡俊峰
 * @CreateDate: 2019/11/20 20:11
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/11/20 20:11
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class ConnectThread {

    public static final String EXECUTOR_TASK = ThreadTask.class.getSimpleName();

    @SuppressWarnings("FinalStaticMethod")
    @NonNull
    public static final ThreadTaskExecutor getExecutor() {
        return ThreadTaskExecutor.getInstance();
    }

    @NonNull
    public static IThreadTask create() {
        return new ThreadTask();
    }

    @NonNull
    public static IThreadTask create(@NonNull final LifecycleOwner lifecycleOwner) {
        return new ThreadTask(lifecycleOwner);
    }
}
