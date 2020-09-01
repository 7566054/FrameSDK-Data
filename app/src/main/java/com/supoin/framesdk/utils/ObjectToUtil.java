package com.supoin.framesdk.utils;

import java.math.BigDecimal;

public class ObjectToUtil {

	public static int getInt(Object str, int defaultValue) {
		try {
			int i = (int) (Double.valueOf((str + "").trim())).doubleValue();
			return i;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return defaultValue;
	}

	public static long getlong(Object str, int defaultValue) {
		try {
			long i = (long) (Double.valueOf((str + "").trim())).doubleValue();
			return i;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return defaultValue;
	}
	
	public static Integer getInteger(Object str, Integer defaultValue) {
		try {
			Integer i = Integer.valueOf((str + "").trim());
			return i;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return defaultValue;
	}

	public static long getLong(Object str, long defaultValue) {
		try {
			long i = (Long.valueOf((str + "").trim())).longValue();
			return i;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return defaultValue;
	}
	
	public static Long getLong(Object str, Long defaultValue) {
		try {
			Long i = Long.valueOf((str + "").trim());
			return i;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return defaultValue;
	}

	public static double getDouble(Object str, double defaultValue) {
		try {
			double i = (Double.valueOf((str + "").trim())).doubleValue();
			return i;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return defaultValue;
	}

	public static String getString(Object str, String defaultValue) {

		try {

			if (String.valueOf(str).equalsIgnoreCase("null")) {
				return defaultValue;
			}

			return String.valueOf(str);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return defaultValue;
	}

	/**
	 * 避免输出科学计数法的字符串
	 * 
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static String getStringFromFloat(Object str, String defaultValue) {

		try {

			if (String.valueOf(str).equalsIgnoreCase("null")) {
				return defaultValue;
			}

			return new BigDecimal(String.valueOf(str)).stripTrailingZeros().toPlainString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return defaultValue;
	}

}
