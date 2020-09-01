package com.supoin.framesdk.utils;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateThreadPool {

	private static ExecutorService cachedThreadPool;

	private ExecutorService createCachedThreadPool() {
		if (cachedThreadPool == null) {
			cachedThreadPool = Executors.newCachedThreadPool();
		}
		return cachedThreadPool;
	}

	@SuppressLint("HandlerLeak")
	public void startThreadPool(final OnInitThreadPool onInitThreadPool) {
		// 创建线程池
		createCachedThreadPool();
		// 定义Handler
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle bundle = msg.getData();
				String error = bundle.getString("error", "");
				onInitThreadPool.onThreadFinsih(error);
			}
		};
		// 加上一个线程
		cachedThreadPool.submit(new Runnable() {
			@Override
			public void run() {
				String error = onInitThreadPool.onThreadDo();
				Message msg = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("error", error);
				msg.setData(bundle);
				handler.sendMessage(msg);
			}
		});
	}

	public void closeThreadPool() {
		if (cachedThreadPool != null) {
			cachedThreadPool.shutdown();
			cachedThreadPool = null;
		}
	}

	// 定义一个初始化多线程池的接口
	public interface OnInitThreadPool {
		//
		abstract String onThreadDo();

		abstract void onThreadFinsih(String error);
	}

}
