package com.cjf.thread.task;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * <p>Title: TaskManager </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2020/2/21 12:03
 */
public class TaskManager {

    private static final String TAG = "TaskManager";

    private CountDownLatch mCountDownLatch;
    private int mCurrentProgress;
    private int mTotalCount;
    private ITask.IProgressUpdate<Integer> mProgressUpdate;
    private ITask.IPostExecute<Boolean> mPostExecute;
    private List<Task.Builder<?, ?, ?>> mTaskList;


    private TaskManager setTotalCount(int totalCount) {
        mTotalCount = totalCount;
        return this;
    }

    public TaskManager setProgressUpdate(@NonNull ITask.IProgressUpdate<Integer> progressUpdate) {
        this.mProgressUpdate = progressUpdate;
        return this;
    }

    public TaskManager setPostExecute(@NonNull ITask.IPostExecute<Boolean> postExecute) {
        this.mPostExecute = postExecute;
        return this;
    }

    /**
     * 线程执行
     *
     * @param taskList 要执行的
     * @return
     */
    public TaskManager setTaskList(@NonNull final List<Task.Builder<?, ?, ?>> taskList) {
        mTaskList = taskList;
        return setTotalCount(mTaskList.size());
    }

    /**
     * 添加计数器任务
     *
     * @param viewActive 当前界面是否还没有销毁
     */
    private void setViewActive(@NonNull final ITask.IIsViewActive viewActive) {
        // 计数器
        mCountDownLatch = new CountDownLatch(mTotalCount);
        Task.<CountDownLatch, Integer, Boolean>newBuilder().setDoInBackground((publishProgress, countDownLatches) -> {
            try {
                countDownLatches[0].await();
                return true;//顺利完成
            } catch (Exception e) {
                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                return false;
            }
        }).setViewActive(viewActive).setPostExecute(mPostExecute).startOnExecutor(mCountDownLatch);
    }

    public void startOnExecutor(@NonNull final ITask.IIsViewActive viewActive) {
        if (mTaskList == null) {
            return;
        }
        for (Task.Builder<?, ?, ?> task : mTaskList) {
            task.setPostExecute(result -> {
                // 一个任务完成
                mCountDownLatch.countDown();
                long currentCount = mCountDownLatch.getCount();
                mCurrentProgress = (int) currentCount * 100 / mTotalCount;
                if (mProgressUpdate != null) {
                    mProgressUpdate.onProgressUpdate(mCurrentProgress);
                }
            }).startOnExecutor();
        }
        setViewActive(viewActive);
    }

    public void start(@NonNull final ITask.IIsViewActive viewActive) {
        if (mTaskList == null) {
            return;
        }
        for (Task.Builder<?, ?, ?> task : mTaskList) {
            task.setPostExecute(result -> {
                // 一个任务完成
                mCountDownLatch.countDown();
                long currentCount = mCountDownLatch.getCount();
                mCurrentProgress = (int) currentCount * 100 / mTotalCount;
                if (mProgressUpdate != null) {
                    mProgressUpdate.onProgressUpdate(mCurrentProgress);
                }
            }).start();
        }
        setViewActive(viewActive);
    }
}
