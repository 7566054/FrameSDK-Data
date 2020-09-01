package com.supoin.framesdk.utils;

import com.supoin.framesdk.R;
import com.supoin.framesdk.base.BaseApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 
 * @ClassName: {@link TimeUtils}
 * @Description: 时间相关工具类
 * @author iBo
 * @date 2014-8-30 下午10:42:21
 * 
 */

public class TimeUtils {

	public static final String DATETIME_FORMAT_DATE = "yyyy-MM-dd HH:mm:ss";
	public static final String DATETIME_FORMAT_DATE_MS = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String DATE_FORMAT_DATE = "yyyy-MM-dd";
	public static final String DATE_FORMAT_DATE_X = "yyyy/MM/dd";
	public static final String DATE_FORMAT_DATE_NO = "yyyyMMdd";
	public static final String DATETIME_FORMAT_DATE_NO = "yyyyMMddHHmmss";
	public static final String DATETIME_FORMAT_DATE_NO_MS = "yyyyMMddHHmmssSSS";
	public static final String DATETIME_FORMAT_DATE_NO_MS_S = "yyMMddHHmmssSSS";
	public static final String DATETIME_FORMAT_DATE_SNO = "yyMMddHHmmss";
	public static final String TIME_FORMAT_DATE_NO = "ddHHmmss";
	public static final String TIME_FORMAT_DATE_D = "HH:mm:ss";
	public static final String MONTH_FORMAT_DATE = "yyyy-MM";
	public static final String DATETIME_FORMAT_MONTH = "MM-dd HH";
	private static long lastClickTime = 0;
	private static String keyOld = "";

	private static final ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>();

	private static final Object object = new Object();

	/**
	 * 获取SimpleDateFormat
	 * 
	 * @param dateFormatStr
	 *            日期格式
	 * @return SimpleDateFormat对象
	 * @throws RuntimeException
	 *             异常：非法日期格式
	 */
	public static SimpleDateFormat getDateFormat(String dateFormatStr) throws RuntimeException {
		SimpleDateFormat dateFormat = threadLocal.get();
		if (dateFormat == null) {
			synchronized (object) {
				if (dateFormat == null) {
					dateFormat = new SimpleDateFormat(dateFormatStr);
					dateFormat.setLenient(false);
					threadLocal.set(dateFormat);
				}
			}
		}
		dateFormat.applyPattern(dateFormatStr);
		return dateFormat;
	}

	/**
	 * 把8位日期转换为加'-'英文形式
	 * 
	 * @Title: toShowENDateFormat
	 * @Description: TODO(Describe the action of this method)
	 * @param :@param dateStr
	 */
	public static String toShowENDateFormat(String dateStr) {
		// 2014-03-03
		return dateStr.substring(0, 4) + "-" + dateStr.substring(4, 6) + "-" + dateStr.substring(6, 8);
	}

	/**
	 * 把8位日期转换为加'年月日'中文形式
	 * 
	 * @Title: toShowENDateFormat
	 * @Description: TODO(Describe the action of this method)
	 * @param :@param dateStr
	 */
	public static String toShowCNDateFormat(String dateStr) {
		// 2014年03月03日
		return dateStr.substring(0, 4) + "年" + dateStr.substring(4, 6) + "月" + dateStr.substring(6, 8) + "日";
	}

	/**
	 * 把'-'或'年月日'日期转换为8位日期形式
	 * 
	 * @Title: toShowENDateFormat
	 * @Description: TODO(Describe the action of this method)
	 * @param :@param dateStr
	 */
	public static String toSqlDateFormat(String dateStr) {
		// 20140808
		return dateStr.substring(0, 4) + dateStr.substring(5, 7) + dateStr.substring(8, 10);
	}

	/**
	 * long time to string
	 * 
	 * @param timeInMillis
	 * @param dateFormat
	 * @return
	 */
	public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
		if (timeInMillis <= 0) {
			return "";
		}
		return dateFormat.format(new Date(timeInMillis));
	}

	/**
	 * currentTimeMillis转换成需要格式的String
	 * 
	 * @Title: getTimeDateFormat
	 * @Description: TODO(Describe the action of this method)
	 * @param :@param timeInMillis
	 * @param :@param dateFormat
	 * @param :@return 参数
	 * @return :String 返回类型
	 * @throws :@param timeInMillis
	 * @throws :@param dateFormat
	 * @throws :@return 异常
	 */
	public static String getTimeDateFormat(long timeInMillis, SimpleDateFormat dateFormat) {
		return getTime(timeInMillis, dateFormat);
	}

	/**
	 * get current time in milliseconds
	 * 
	 * @return
	 */
	public static long getCurrentTimeInLong() {
		return System.currentTimeMillis();
	}

	/**
	 * 把8位日期格式的String转换为long类型
	 * 
	 * @Title: getLongTime
	 * @Description: TODO(Describe the action of this method)
	 * @param :@param dateStr
	 * @param :@param simpleDateFormat
	 */
	public static long getLongTime(String dateStr, SimpleDateFormat simpleDateFormat) {
		Date date = null;
		try {
			date = simpleDateFormat.parse(dateStr);
			long longTime = date.getTime();
			return longTime;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static int getCurrentYear() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		return year;
	}

	public static int getCurrentMonth() {
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH) + 1;
		return month;
	}

	public static int getCurrentDay() {
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DATE);
		return day;
	}

	/**
	 * get current time in milliseconds
	 * 
	 * @return
	 */
	public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
		return getTime(getCurrentTimeInLong(), dateFormat);
	}

	/**
	 * 限制连续点击
	 * 
	 * @Title: isFastDoubleClick
	 * @Description: TODO(Describe the action of this method)
	 * @param :@param key 按键的标识 用来判断是否是同一个按键按下
	 * @param :@param timeMillis 间隔的毫秒
	 * @param :@return 参数true连续点击了 false没有连续点击
	 * @return :boolean 返回类型
	 * @throws :@return 异常
	 */
	public static boolean isFastDoubleClick(String key, long timeMillis) {
		if (key != null) {
			if (!keyOld.equals(key)) {
				lastClickTime = 0;
				keyOld = key;
			}
		}
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < timeMillis) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	/**
	 * 
	 * @Title: compareDateTime
	 * @Description: 比较日期大小
	 * @param :@param firstStr
	 * @param :@param secondStr
	 */
	public static boolean compareDateTime(String firstStr, String secondStr, SimpleDateFormat simpleDateFormat) {
		Date firstDate = null;
		Date secondDate = null;
		try {
			firstDate = simpleDateFormat.parse(firstStr);
			secondDate = simpleDateFormat.parse(secondStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long firstLongTime = firstDate.getTime();
		long secondLongTime = secondDate.getTime();
		if (firstLongTime > secondLongTime) {
			return false;
		} else {
			return true;
		}
	}

	public static String getDateStr(Date date, SimpleDateFormat dateFormat) {
		if (date == null) {
			return "";
		}

		String str = dateFormat.format(date);

		return str;
	}

	/**
	 * get current time with milliseconds
	 * 
	 * @return
	 */
	public static String getTimeWithMS(long timeInMillis) {
		return getTime(timeInMillis, getDateFormat(DATETIME_FORMAT_DATE_MS));
	}

	public static String getTimeBefor(long timeBefor) {

		long time = System.currentTimeMillis();

		if (timeBefor <= 0) {
			return BaseApplication.getInstance().getString(R.string.unknown_time);
		}

		// 相差的毫秒 = 当前时间 - 参数时间
		timeBefor = time - timeBefor;

		//
		if (timeBefor >= 0 && timeBefor < 1000) {
			return BaseApplication.getInstance().getString(R.string.just_now);
		} else if (timeBefor > 1000 && timeBefor < 60000) {// 60秒以内
			timeBefor = timeBefor / 1000;
			return timeBefor + BaseApplication.getInstance().getString(R.string.second_ago);
		} else if (timeBefor >= 60000 && timeBefor < 3600000) {// 60分钟以内
			timeBefor = timeBefor / (1000 * 60);
			return timeBefor + BaseApplication.getInstance().getString(R.string.min_ago);
		} else if (timeBefor >= 3600000 && timeBefor < 86400000) {// 24小时以内
			timeBefor = timeBefor / (1000 * 60 * 60);
			return timeBefor + BaseApplication.getInstance().getString(R.string.hour_ago);
		} else if (timeBefor >= 86400000) {// 超过一天
			timeBefor = timeBefor / (1000 * 60 * 60 * 24);
			return timeBefor + BaseApplication.getInstance().getString(R.string.day_ago);
		} else {
			return BaseApplication.getInstance().getString(R.string.unknown_time);
		}
	}

}
