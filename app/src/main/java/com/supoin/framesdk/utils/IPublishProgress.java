package com.supoin.framesdk.utils;

/**
 * @Author Lu An
 * 创建时间  2018/11/1
 * 描述 IDoInBackground接口中，doInBackground方法与重写AnyncTask的方法并不一样，
 * 多了一个接口IPublishProgress参数
 */
public interface IPublishProgress<Progress>{
    void showProgress(Progress... values);
}