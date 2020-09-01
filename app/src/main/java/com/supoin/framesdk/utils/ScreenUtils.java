package com.supoin.framesdk.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * ScreenUtils
 * <ul>
 * <strong>Convert between dp and sp</strong>
 * <li>{@link ScreenUtils#dpToPx(Context, float)}</li>
 * <li>{@link ScreenUtils#pxToDp(Context, float)}</li>
 * </ul>
 * 
 */
public class ScreenUtils {

	private static int screenShort = 0;
	private static int screenLong = 0;
	public static float dpToPx(Context context, float dp) {
		if (context == null) {
			return -1;
		}
		return dp * context.getResources().getDisplayMetrics().density;
	}

	public static float pxToDp(Context context, float px) {
		if (context == null) {
			return -1;
		}
		return px / context.getResources().getDisplayMetrics().density;
	}

	public static int dpToPxInt(Context context, float dp) {
		return (int) (dpToPx(context, dp) + 0.5f);
	}

	public static int pxToDpCeilInt(Context context, float px) {
		return (int) (pxToDp(context, px) + 0.5f);
	}

	/**
	 * 
	* @Title: getShortEdge 
	* @Description: 获取屏幕长边的长度
	* @param :@param context
	* @param :@return    参数 
	* @return :int    返回类型 
	* @throws :@param context
	* @throws :@return    异常
	 */
	public static int getLongEdge(Context context) {
		if (getScreenlong() == 0) {
			boolean land = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
			WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			Display display = windowManager.getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x;
			int height = size.y;
			if (land) {
				screenLong = width;
			} else {
				screenLong = height;
			}
			return screenLong;
		}else {
			return screenLong;
		}
		
	}
	
	/**
	 * 
	* @Title: getShortEdge 
	* @Description: 获取屏幕短边的长度
	* @param :@param context
	* @param :@return    参数 
	* @return :int    返回类型 
	* @throws :@param context
	* @throws :@return    异常
	 */
	public static int getShortEdge(Context context) {
		if (screenShort==0) {
			boolean land = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
			WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			Display display = windowManager.getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x;
			int height = size.y;
			if (land) {
				screenShort = height;
			} else {
				screenShort = width;
			}
			return screenShort;
		}else {
			return screenShort;
		}
		
	}

	/**
	 * 
	* @Title: getProportionDistance 
	* @Description: 按照屏幕比例计算长度
	* @param :@param context
	* @param :@param pxDistance
	* @param :@return    参数 
	* @return :float    返回类型 
	* @throws :@param context
	* @throws :@param pxDistance
	* @throws :@return    异常
	 */
	public static float getProportionWidth(Context context, int pxLength){
		float length;
		length = pxLength/getShortEdge(context);
		return length;
	}
	
	/**
	 * 
	* @Title: getProportionDistance 
	* @Description: 按照屏幕比例计算长度
	* @param :@param context
	* @param :@param pxDistance
	* @param :@return    参数 
	* @return :float    返回类型 
	* @throws :@param context
	* @throws :@param pxDistance
	* @throws :@return    异常
	 */
	public static float getProportionHeight(Context context, int pxLength){
		float length;
		length = (float)pxLength/getLongEdge(context);
		return length;
	}
	
	/**
	 * 
	 * @Title: getFontSize
	 * @Description: 字体大小
	 * @param :@param context
	 * @param :@param textSize 字像素
	 * @param :@return 参数
	 */
	public static int getFontSize(Context context, int textSize) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		boolean land = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
		windowManager.getDefaultDisplay().getMetrics(dm);
		int screenHeight;
		if (land) {
			screenHeight = dm.widthPixels;
		} else {
			screenHeight = dm.heightPixels;
		}
		int rate = (int) (textSize * (float) screenHeight / 1280);
		return rate;
	}

	public static int getScreenlong() {
		return screenLong;
	}
	
	/**
	 * 获取屏幕的宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context)
	{
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	/**
	 * 获取屏幕的高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context)
	{
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}

}
