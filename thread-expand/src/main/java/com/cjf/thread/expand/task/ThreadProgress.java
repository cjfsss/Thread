package com.cjf.thread.expand.task;


import java.io.Serializable;

/**
 * @ProjectName: MobileEnforcement-BeiJing
 * @Package: com.mapuni.android.core.net.thread
 * @ClassName: ThreadProgress
 * @Description: java类作用描述
 * @Author: 蔡俊峰
 * @CreateDate: 2019-7-30 13:47
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019-7-30 13:47
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class ThreadProgress implements Serializable {

    private static final long serialVersionUID = 8550337799498100781L;
    /**
     * 当前哪个新出
     */
    public int position = 0;
    /**
     * 单个线程的进度
     */
    public long currentCount = 0;

    /**
     * 单个线程的总大小
     */
    public long totalCount = 0;
    /**
     * 当个线程的进度 0-100
     */
    public int currentProgress = 0;

    /**
     * 单个线程是否成功
     */
    public boolean isCurrentSuccess = false;
    /**
     * 单个线程的异常
     */
    public Throwable throwable;
    /**
     * 已经完成几个线程
     */
    public int threadCurrentCount = 0;
    /**
     * 一个几个线程
     */
    public int threadTotalCount = 0;
    /**
     * 线程的的进度 0-100
     */
    public int threadCurrentProgress = 0;

    public ThreadProgress copy() {
        ThreadProgress serviceResult = new ThreadProgress();
        serviceResult.position = position;
        serviceResult.currentCount = currentCount;
        serviceResult.totalCount = totalCount;
        serviceResult.currentProgress = currentProgress;
        serviceResult.isCurrentSuccess = isCurrentSuccess;
        serviceResult.throwable = throwable;
        serviceResult.threadCurrentCount = threadCurrentCount;
        serviceResult.threadTotalCount = threadTotalCount;
        serviceResult.threadCurrentProgress = threadCurrentProgress;
        return serviceResult;
    }

    @SuppressWarnings("EqualsReplaceableByObjectsCall")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThreadProgress that = (ThreadProgress) o;

        if (position != that.position) return false;
        if (currentCount != that.currentCount) return false;
        if (totalCount != that.totalCount) return false;
        if (currentProgress != that.currentProgress) return false;
        if (isCurrentSuccess != that.isCurrentSuccess) return false;
        if (threadCurrentCount != that.threadCurrentCount) return false;
        if (threadTotalCount != that.threadTotalCount) return false;
        if (threadCurrentProgress != that.threadCurrentProgress) return false;
        return throwable != null ? throwable.equals(that.throwable) : that.throwable == null;
    }

    @Override
    public int hashCode() {
        int result = position;
        result = 31 * result + (int) (currentCount ^ (currentCount >>> 32));
        result = 31 * result + (int) (totalCount ^ (totalCount >>> 32));
        result = 31 * result + currentProgress;
        result = 31 * result + (isCurrentSuccess ? 1 : 0);
        result = 31 * result + (throwable != null ? throwable.hashCode() : 0);
        result = 31 * result + threadCurrentCount;
        result = 31 * result + threadTotalCount;
        result = 31 * result + threadCurrentProgress;
        return result;
    }

    @Override
    public String toString() {
        return "ThreadProgress{" +
                "position=" + position +
                ", currentCount=" + currentCount +
                ", totalCount=" + totalCount +
                ", currentProgress=" + currentProgress +
                ", isCurrentSuccess=" + isCurrentSuccess +
                ", throwable=" + throwable +
                ", threadCurrentCount=" + threadCurrentCount +
                ", threadTotalCount=" + threadTotalCount +
                ", threadCurrentProgress=" + threadCurrentProgress +
                '}';
    }
}
