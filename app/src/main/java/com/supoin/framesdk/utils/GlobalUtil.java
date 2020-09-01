package com.supoin.framesdk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 共享内存操作，包括保存、获取
 * Created by zwei on 2019/9/1.
 */
public class GlobalUtil {
	private final String FILE_NAME = "CONFIGURE";


	private static GlobalUtil instance;
	public static GlobalUtil getInstance() {
		if (null == instance) {
			instance = new GlobalUtil();
		}
		return instance;
	}

	public boolean saveValue(Context context, String key, String value) {
		Editor editor = context.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE).edit();
		editor.putString(key, value);

		return editor.commit();
	}

	public boolean saveValue(Context context, String key, int value) {
		Editor editor = context.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE).edit();
		editor.putInt(key, value);

		return editor.commit();
	}

	public boolean saveValue(Context context, String key, long value) {
		Editor editor = context.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE).edit();
		editor.putLong(key, value);

		return editor.commit();
	}

	public boolean saveValue(Context context, String key, boolean value) {
		Editor editor = context.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE).edit();
		editor.putBoolean(key, value);

		return editor.commit();
	}

	public String getValue(Context context, String key, String defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE);
		return sp.getString(key, defaultValue);
	}

	public int getValue(Context context, String key, int defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE);
		return sp.getInt(key, defaultValue);
	}

	public long getValue(Context context, String key, long defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE);
		return sp.getLong(key, defaultValue);
	}

	public boolean getValue(Context context, String key, boolean defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE);
		return sp.getBoolean(key, defaultValue);
	}

	public boolean saveValue(Context context, String key, Object value) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(value);
		String objString = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));

		Editor editor = context.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE).edit();
		editor.putString(key, objString);
		objectOutputStream.close();
		return editor.commit();
	}

	public Object getValue(Context context, String key) throws Exception {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE);
		String str = sp.getString(key, "");
		if (str.length() <= 0)
			return null;
		Object obj = null;
		byte[] mobileBytes = Base64.decode(str.toString().getBytes(), Base64.DEFAULT);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
		ObjectInputStream objectInputStream;
		objectInputStream = new ObjectInputStream(byteArrayInputStream);
		obj = objectInputStream.readObject();
		objectInputStream.close();
		return obj;
	}
}
