package com.cjf.thread.expand;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.cjf.thread.expand.task.Task;
import com.cjf.thread.expand.task.TaskManager;

import java.util.ArrayList;
import java.util.List;

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

    @NonNull
    @Keep
    public static ThreadTaskExecutor getThread() {
        return ThreadTaskExecutor.getInstance();
    }

    @SuppressWarnings("rawtypes")
    @NonNull
    @Keep
    public static TaskManager task(@NonNull Task.Builder taskBuilder) {
        ArrayList<Task.Builder> builders = new ArrayList<>();
        builders.add(taskBuilder);
        return task(builders);
    }

    @NonNull
    @Keep
    public static TaskManager task(@NonNull final List<Task.Builder> taskList) {
        return new TaskManager().setTaskList(taskList);
    }
}
