package com.cjf.thread.task;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.NonNull;

/**
 * <p>Title: IPreExecute </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2020/2/21 11:15
 */
public interface ITask {

    interface IPreExecute {

        void onPreExecute();
    }

    interface IDoInBackground<Params, Progress, Result> {
        Result doInBackground(@NonNull IPublishProgress<Progress> publishProgress, @NonNull Params... params);
    }

    interface IProgressUpdate<Progress> {
        void onProgressUpdate(@NonNull Progress... values);
    }

    interface IPostExecute<Result> {
        void onPostExecute(@NonNull Result result);
    }

    interface IPostExecuteListener<Result> {
        /**
         * 添加监听
         */
        void addListener(@NonNull IPostExecute<Result> listener);

        /**
         * 移除监听
         */
        void removeListener(@NonNull IPostExecute<Result> listener);

        /**
         * 移除所有监听
         */
        void removeAll();
    }

    interface IPublishProgress<Progress> {
        void showProgress(@NonNull Progress... values);
    }

    interface IIsViewActive {
        boolean isViewActive();
    }

    /**
     * true 没有销毁
     *
     * @param activity 上下文
     * @return true 没有销毁
     */
    static boolean isViewActive(@NonNull Activity activity) {
        return !(activity.isFinishing() ||
                 (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()));
    }
}
