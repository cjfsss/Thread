package com.cjf.thread.task;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: Task </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @version : 1.0
 * @date : 2020/2/21 11:23
 */
public class Task<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>
        implements ITask.IPublishProgress<Progress>, ITask.IIsViewActive,
                   ITask.IPostExecuteListener<Result> {

    private ITask.IPreExecute mPreExecute;
    private ITask.IProgressUpdate<Progress> mProgressUpdate;
    private ITask.IDoInBackground<Params, Progress, Result> mDoInBackground;
    private ITask.IIsViewActive mViewActive;
    private final List<ITask.IPostExecute<Result>> mPostExecuteList = new ArrayList<>();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mPreExecute != null) mPreExecute.onPreExecute();
    }

    @SafeVarargs
    @Override
    protected final void onProgressUpdate(@NonNull Progress... values) {
        super.onProgressUpdate(values);
        if (mProgressUpdate != null) mProgressUpdate.onProgressUpdate(values);
    }

    @SafeVarargs
    @Override
    public final Result doInBackground(@Nullable Params... params) {
        return mDoInBackground == null ? null : mDoInBackground.doInBackground(this, params);
    }

    @Override
    protected void onPostExecute(@NonNull Result result) {
        super.onPostExecute(result);
        if (!mPostExecuteList.isEmpty() && isViewActive()) {
            for (ITask.IPostExecute<Result> postExecute : mPostExecuteList) {
                postExecute.onPostExecute(result);
            }
        }
        removeAll();
    }

    @SafeVarargs
    @Override
    public final void showProgress(@NonNull Progress... values) {
        this.publishProgress(values);
    }

    @SafeVarargs
    public final AsyncTask<Params, Progress, Result> startOnExecutor(Params... params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return super.executeOnExecutor(THREAD_POOL_EXECUTOR, params);
        } else {
            return super.execute(params);
        }
    }

    @SafeVarargs
    public final AsyncTask<Params, Progress, Result> start(Params... params) {
        return super.execute(params);
    }

    @Override
    public boolean isViewActive() {
        return mViewActive == null || mViewActive.isViewActive();
    }

    public static <Params, Progress, Result> Builder<Params, Progress, Result> newBuilder() {
        return new Builder<>();
    }

    @Override
    public void addListener(@NonNull ITask.IPostExecute<Result> resultIPostExecute) {
        if (isViewActive() && !mPostExecuteList.contains(resultIPostExecute)) {
            mPostExecuteList.add(resultIPostExecute);
        }
    }

    @Override
    public void removeListener(@NonNull ITask.IPostExecute<Result> resultIPostExecute) {
        if (!mPostExecuteList.isEmpty() && isViewActive()) {
            mPostExecuteList.remove(resultIPostExecute);
        }
    }

    @Override
    public void removeAll() {
        mPostExecuteList.clear();
    }

    public static class Builder<Params, Progress, Result> {

        private final Task<Params, Progress, Result> mAsyncTask;

        private Builder() {
            mAsyncTask = new Task<>();
        }

        @NonNull
        public Builder<Params, Progress, Result> setPreExecute(@NonNull ITask.IPreExecute preExecute) {
            mAsyncTask.mPreExecute = preExecute;
            return this;
        }

        @NonNull
        public Builder<Params, Progress, Result> setProgressUpdate(@NonNull ITask.IProgressUpdate<Progress> progressUpdate) {
            mAsyncTask.mProgressUpdate = progressUpdate;
            return this;
        }

        @NonNull
        public Builder<Params, Progress, Result> setDoInBackground(@NonNull ITask.IDoInBackground<Params, Progress, Result> doInBackground) {
            mAsyncTask.mDoInBackground = doInBackground;
            return this;
        }

        @NonNull
        public Builder<Params, Progress, Result> setViewActive(@NonNull ITask.IIsViewActive viewActive) {
            mAsyncTask.mViewActive = viewActive;
            return this;
        }

        @NonNull
        public Builder<Params, Progress, Result> setPostExecute(@NonNull ITask.IPostExecute<Result> postExecute) {
            mAsyncTask.addListener(postExecute);
            return this;
        }

        @SafeVarargs
        @NonNull
        public final AsyncTask<Params, Progress, Result> startOnExecutor(Params... params) {
            return mAsyncTask.startOnExecutor(params);
        }

        @SafeVarargs
        @NonNull
        public final AsyncTask<Params, Progress, Result> start(Params... params) {
            return mAsyncTask.start(params);
        }
    }
}
