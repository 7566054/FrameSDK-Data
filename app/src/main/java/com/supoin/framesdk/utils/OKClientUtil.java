package com.supoin.framesdk.utils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.blankj.utilcode.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Author zwei
 * 创建时间  2018/3/26 0026
 * 描述  类的用途 封装OkHttp3的工具类 用单例设计模式
 */

public class OKClientUtil {

    /**
     * 懒汉 安全 加同步
     * 私有的静态成员变量 只声明不创建
     * 私有的构造方法
     * 提供返回实例的静态方法
     */

    private volatile static OKClientUtil okClientUtil = null;
    private OKClientUtil() {}
    public static OKClientUtil getInstance() {
        if (okClientUtil == null) {
            //加同步安全
            synchronized (OKClientUtil.class) {
                if (okClientUtil == null) {
                    okClientUtil = new OKClientUtil();
                }
            }
        }
        return okClientUtil;
    }

    private static Handler mHandler = null;

    public synchronized static Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler();
        }
        return mHandler;
    }

    private OkHttpClient mOkHttpClient = null;
    private synchronized  OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {
            //判空 为空创建实例
            /* *
             * 和OkHttp2.x有区别的是不能通过OkHttpClient直接设置超时时间和缓存了，而是通过OkHttpClient.Builder来设置，
             * 通过builder配置好OkHttpClient后用builder.build()来返回OkHttpClient，
             * 所以我们通常不会调用new OkHttpClient()来得到OkHttpClient，而是通过builder.build()：
            **/
            //OkHttp3拦截器
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {

                }
            });
            //Okhttp3的拦截器日志分类 4种
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            mOkHttpClient = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS)
                    //添加OkHttp3的拦截器
                    .addInterceptor(httpLoggingInterceptor)
                    .writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
                    .build();
        }
        return mOkHttpClient;
    }

    /**
     * get请求
     * 参数1 url
     * 参数2 回调Callback
     */

    public void doGet(String url, Callback callback) {

        //创建OkHttpClient请求对象
        OkHttpClient okHttpClient = getOkHttpClient();
        //创建Request
        Request request = new Request.Builder().url(url).build();
        //得到Call对象
        Call call = okHttpClient.newCall(request);
        //执行异步请求
        call.enqueue(callback);
    }

    public Response doGet(String url) throws IOException {

        //创建OkHttpClient请求对象
        OkHttpClient okHttpClient = getOkHttpClient();
        //创建Request
        Request request = new Request.Builder().url(url).build();
        //得到Call对象
        Call call = okHttpClient.newCall(request);
        //执行同步请求
        return call.execute();
    }

    public void doGet(String url, Map<String, String> headers, Callback callback) {

        Request.Builder builder = new Request.Builder().url(url);

        //遍历集合
        for (String key : headers.keySet()) {
            builder.addHeader(key, headers.get(key));
        }
        Request request = builder.get().build();
        Call call = getOkHttpClient().newCall(request);
        call.enqueue(callback);
    }

    /**
     * post请求
     * 参数1 url
     * 参数2 回调Callback
     */

    public void doPost(String url, Map<String, String> params, Callback callback) {
        //创建OkHttpClient请求对象
        OkHttpClient okHttpClient = getOkHttpClient();
        //3.x版本post请求换成FormBody 封装键值对参数
        FormBody.Builder builder = new FormBody.Builder();

        //遍历集合
        for (String key : params.keySet()) {
            builder.add(key, params.get(key));
        }

        //创建Request
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }

    /**
     * post请求上传文件
     * 参数1 url
     * 参数2 回调Callback
     */
    public void uploadPic(String url, File file, String fileName) {
        //创建OkHttpClient请求对象
        OkHttpClient okHttpClient = getOkHttpClient();
        //创建RequestBody 封装file参数
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        //创建RequestBody 设置类型等
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", fileName, fileBody).build();
        //创建Request
        Request request = new Request.Builder().url(url).post(requestBody).build();

        //得到Call
        Call call = okHttpClient.newCall(request);
        //执行请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //上传成功回调 目前不需要处理
            }
        });

    }

    /**
     * Post请求发送JSON数据
     * 参数一：请求Url
     * 参数二：请求的JSON
     * 参数三：请求回调
     */
    public void doPostJson(String url, String jsonParams, Callback callback) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonParams);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Call call = getOkHttpClient().newCall(request);
        call.enqueue(callback);
    }

    /**
     * 同步POST请求：JSON数据
     * 参数一：请求Url
     * 参数二：请求的JSON
     */
    public Response doPostJson(String url, String jsonParams) throws IOException {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonParams);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Call call = getOkHttpClient().newCall(request);
        return call.execute();
    }

    /**
     * 同步带头的POST请求：JSON数据
     * 参数一：请求Url
     * 参数二：请求的头
     * 参数三：请求的JSON
     */
    public Response doPostJson(String url, Map<String, String> headers, String jsonParams) throws IOException {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonParams);
        Request.Builder builder = new Request.Builder().url(url);
        //遍历集合
        for (String key : headers.keySet()) {
            builder.addHeader(key, headers.get(key));
        }
        Request request = builder.post(requestBody).build();
        Call call = getOkHttpClient().newCall(request);
        return call.execute();
    }

    public void doPostJson(String url, Map<String, String> headers, String jsonParams, Callback callback) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonParams);
        Request.Builder builder = new Request.Builder().url(url);

        //遍历集合
        for (String key : headers.keySet()) {
            builder.addHeader(key, headers.get(key));
        }
        Request request = builder.post(requestBody).build();
        Call call = getOkHttpClient().newCall(request);
        call.enqueue(callback);
    }

    /**
     * @param url 下载连接
     * @param saveFileName 储存下载文件的SDCard目录
     * @param listener 下载监听
     */
    public String downLoadFile(final String url, final String saveFileName, final OnDownloadFileListener listener) {
        Request request = new Request.Builder().url(url).build();
        // 异步请求
        Response response = null;
        InputStream is = null;
        byte[] buf = new byte[1024];
        int len = 0;
        FileOutputStream fos = null;
        try {
            response = getOkHttpClient().newCall(request).execute();

            if (response.isSuccessful()) {

                File file = new File(saveFileName);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                is = response.body().byteStream();
                fos = new FileOutputStream(file);
                int size = 0;
                long total = response.body().contentLength();
                while ((size = is.read(buf)) != -1) {
                    len += size;
                    fos.write(buf, 0, size);

                    listener.onDownloading(len, total);
                }
                fos.flush();

                return "OK";
            } else {
                return response.message();
            }
        } catch (IOException ex) {
            return ex.getMessage();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public interface OnDownloadFileListener {
        void onDownloading(int downloadedSize, long fileSize);
    }
}
