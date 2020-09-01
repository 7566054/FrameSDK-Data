package com.supoin.framesdk.utils;

import android.os.AsyncTask;
import android.os.Build;


/**
 * @Author Lu An
 * 创建时间  2018/11/1
 * 描述 采用Builder模式将AsyncTask封装成外观上类似Rxjava的链式调用的形式
 */

public class AsyncTaskUtil<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>
        implements IPublishProgress<Progress> {

    private IPreExecute mPreExecute;
    private IProgressUpdate<Progress> mProgressUpdate;
    private IDoInBackground<Params, Progress, Result> mDoInBackground;
    private IIsViewActive mViewActive;
    private IPostExecute<Result> mPostExecute;

    private AsyncTaskUtil() { }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mPreExecute != null) mPreExecute.onPreExecute();
    }

    /**
     * IDoInBackground接口中，doInBackground方法与重写AnyncTask的方法并不一样，多了一个接口IPublishProgress参数.
     * @param params 参数
     * @return
     */
    @Override
    public Result doInBackground(Params... params) {
        return mDoInBackground == null ? null : mDoInBackground.doInBackground(this, params);
    }

    /**
     * 在实现方法中showProgress中，调用AsyncTask的publishProgress(values)方法（publishProgress(values)是个final方法，不能重写）
     *  isViewActive() 如果处于活跃状态，则回调IPostExecute接口，否则不回调。
     * @param values 进度
     */
    @SafeVarargs
    @Override
    protected final void onProgressUpdate(Progress... values) {
        super.onProgressUpdate(values);
        if (mProgressUpdate != null && (mViewActive == null || mViewActive.isViewActive())){
            mProgressUpdate.onProgressUpdate(values);
        }
    }

    /**
     * IIsViewActive接口的引用，在onPostExecute方法中通过它来判断所属Activity是否处于活跃状态，
     * 如果处于活跃状态，则回调IPostExecute接口，否则不回调。
     * @param result 返回结果
     */
    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        if (mPostExecute != null && (mViewActive == null || mViewActive.isViewActive())){
            mPostExecute.onPostExecute(result);
        }
    }

    //开始异步任务
    @SafeVarargs
    public final AsyncTask<Params, Progress, Result> start(Params... params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return super.executeOnExecutor(THREAD_POOL_EXECUTOR, params);
        } else {
            return super.execute(params);
        }
    }

    //显示进度
    @Override
    public void showProgress(Progress[] values) {
        this.publishProgress(values);
    }

    //构建Builder
    public static <Params, Progress, Result> Builder<Params, Progress, Result> builder() {
        return new Builder<>();
    }

    /**
     * 添加一个Builder,来简化它的构建，同时私有化其构造方法，避免外部直接实例化其对象。
     * @param <Params>
     * @param <Progress>
     * @param <Result>
     */
    public static class Builder<Params, Progress, Result> {

        private final AsyncTaskUtil<Params, Progress, Result> mAsyncTask;
        public Builder() {
            mAsyncTask = new AsyncTaskUtil<>();
        }

        public Builder<Params, Progress, Result> setPreExecute(IPreExecute preExecute) {
            mAsyncTask.mPreExecute = preExecute;
            return this;
        }

        public Builder<Params, Progress, Result> setProgressUpdate(IProgressUpdate<Progress> progressUpdate) {
            mAsyncTask.mProgressUpdate = progressUpdate;
            return this;
        }

        public Builder<Params, Progress, Result> setDoInBackground(IDoInBackground<Params, Progress, Result> doInBackground) {
            mAsyncTask.mDoInBackground = doInBackground;
            return this;
        }

        public Builder<Params, Progress, Result> setViewActive(IIsViewActive viewActive) {
            mAsyncTask.mViewActive = viewActive;
            return this;
        }

        public Builder<Params, Progress, Result> setPostExecute(IPostExecute<Result> postExecute) {
            mAsyncTask.mPostExecute = postExecute;
            return this;
        }

        @SafeVarargs
        public final AsyncTask<Params, Progress, Result> start(Params... params) {
            return mAsyncTask.start(params);
        }
    }

    public interface IDoInBackground<Params,Progress,Result> {
        Result doInBackground(IPublishProgress<Progress> publishProgress, Params... params);
    }

    /**
     * @Author Lu An
     * 创建时间  2018/11/1
     * 描述  在方法doInBackground执行结束开始回调方法onPostExecute之前，用来判断所属Activity是否依然处于活跃状态，
     * 如果处于活跃状态则回调方法onPostExecute，如果处于非活跃状态则不回调，避免回调后操作UI产生空指针异常。
     */
    public interface IIsViewActive {
        boolean isViewActive();
    }

    public interface IPostExecute<Result> {
        void onPostExecute(Result result);
    }

    public interface IPreExecute {
        void onPreExecute();
    }

    public interface IProgressUpdate<Progress>{
        void onProgressUpdate(Progress... values);
    }
}
