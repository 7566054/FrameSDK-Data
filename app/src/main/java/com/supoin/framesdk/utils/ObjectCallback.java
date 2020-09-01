package com.supoin.framesdk.utils;

import android.os.Handler;
import android.util.MalformedJsonException;

import com.blankj.utilcode.util.GsonUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class ObjectCallback<T> implements Callback {
    private Handler handler = OKClientUtil.getInstance().getHandler();
    private T t = null;
    //主线程处理
    public abstract void onUi(T t);

    //主线程处理
    public abstract void onFailed(Call call, IOException e);

    //请求失败
    @Override
    public void onFailure(final Call call, final IOException e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onFailed(call, e);
            }
        });
    }

    //请求json 并直接返回泛型的对象 主线程处理
    @Override
    public void onResponse(Call call, Response response) throws IOException {


        if (response.body() == null)
        {
            onFailure(call, new IOException("server callback result is NULL"));
            return;
        }

        String json = response.body().string();

        //有可能返回404等访问失败html代码
        if (!CommUtil.getInstance().isJsonString(json)) {
            onFailure(call, new MalformedJsonException("server result is error  code = "
                    + response.code() + " message = " + response.message()));
            return;
        }
        try {
            Class clz = this.getClass();
            ParameterizedType type = (ParameterizedType) clz.getGenericSuperclass();
            Type[] types = type.getActualTypeArguments();
            Class<T> cls = (Class<T>) types[0];

            t = GsonUtils.fromJson(json, cls);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onUi(t);
                }
            });

        } catch (Exception JsonSyntaxException) {
            throw JsonSyntaxException;
        }
    }
}
