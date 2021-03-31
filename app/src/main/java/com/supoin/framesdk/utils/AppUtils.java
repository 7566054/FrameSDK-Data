package com.supoin.framesdk.utils;


import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.supoin.framesdk.R;
import com.supoin.framesdk.base.BaseApplication;

import java.io.File;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;


/**
 * 
 * @ClassName: {@link AppUtils}
 * @Description: App 相关工具方法
 * @author iBo
 * @date 2014-9-25 下午12:59:47
 * 
 */
public class AppUtils {


	public static String getStackTrace(Throwable e) {
		if (e == null) {
			return "";
		}
		StringBuilder str = new StringBuilder();
		str.append(e.getMessage()).append("\n");
		for (int i = 0; i < e.getStackTrace().length; i++) {
			str.append(e.getStackTrace()[i]).append("\n");
		}
		return str.toString();
	}

	/**
	 * 获取缓存大小
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String getTotalCacheSize(Context context) throws Exception {
		long cacheSize = getFolderSize(context.getCacheDir());
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			cacheSize += getFolderSize(context.getExternalCacheDir());
		}
		return getFormatSize(cacheSize);
	}

	/**
	 * 清理缓存
	 * 
	 * @param context
	 */
	public static void clearAllCache(Context context) {
		deleteDir(context.getCacheDir());
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			deleteDir(context.getExternalCacheDir());
		}
	}

	private static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	// 获取文件
	// Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/
	// 目录，一般放一些长时间保存的数据
	// Context.getExternalCacheDir() -->
	// SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
	public static long getFolderSize(File file) throws Exception {
		long size = 0;
		try {
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				// 如果下面还有文件
				if (fileList[i].isDirectory()) {
					size = size + getFolderSize(fileList[i]);
				} else {
					size = size + fileList[i].length();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}

	/**
	 * 格式化单位
	 *
	 * @param size
	 * @return
	 */
	public static String getFormatSize(double size) {
		double kiloByte = size / 1024;
		if (kiloByte < 1) {
			// return size + "Byte";
			return BaseApplication.getInstance().getString(R.string.less_than_1KB);
		}

		double megaByte = kiloByte / 1024;
		if (megaByte < 1) {
			BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
			return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
		}

		double gigaByte = megaByte / 1024;
		if (gigaByte < 1) {
			BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
			return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
		}

		double teraBytes = gigaByte / 1024;
		if (teraBytes < 1) {
			BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
			return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
		}
		BigDecimal result4 = new BigDecimal(teraBytes);
		return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
	}




	/**
	 * 清除条码的所有空格
	 * @param str
	 * @return
	 */
	public static String clearPaces(String str){
		String value = "";
		if (!TextUtils.isEmpty(str)){
			value = str.replace(" ", "");
		}

		return value;
	}

	/**
	 * 通知栏缩进
	 *
	 * @param context
	 */
	public static void collapseStatusBar(Context context) {
		try {
			Object statusBarManager = context.getSystemService("statusbar");
			Method collapse;
			if (Build.VERSION.SDK_INT <= 16) {
				collapse = statusBarManager.getClass().getMethod("collapse");
			} else {
				collapse = statusBarManager.getClass().getMethod("collapsePanels");
			}
			collapse.invoke(statusBarManager);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	/**
	 * 判断
	 *
	 * @param context
	 * @return true 前台 false 后台
	 */
	public static boolean isForeground(Context context) {

		// IMPORTANCE_BACKGROUND = 400后台
		// IMPORTANCE_EMPTY = 500空进程
		// IMPORTANCE_FOREGROUND = 100在屏幕最前端、可获取到焦点可理解为Activity生命周期的OnResume();
		// IMPORTANCE_SERVICE = 300 在服务中
		// IMPORTANCE_VISIBLE = 200 在屏幕前端、获取不到焦点可理解为Activity生命周期的OnStart();
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND || appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE
						|| appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_EMPTY) {
					return false;
				} else {
					return true;
				}
			}
		}
		return false;
	}
}
