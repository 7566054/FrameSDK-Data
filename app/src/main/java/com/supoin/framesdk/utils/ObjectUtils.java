package com.supoin.framesdk.utils;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/**
 * Object Utils
 * 
 */
public class ObjectUtils {

	/**
	 * compare two object
	 * 
	 * @param actual
	 * @param expected
	 * @return <ul>
	 *         <li>if both are null, return true</li>
	 *         <li>return actual.{@link Object#equals(Object)}</li>
	 *         </ul>
	 */
	public static boolean isEquals(Object actual, Object expected) {
		return actual == expected || (actual == null ? expected == null : actual.equals(expected));
	}

	/**
	 * convert long array to Long array
	 * 
	 * @param source
	 * @return
	 */
	public static Long[] transformLongArray(long[] source) {
		Long[] destin = new Long[source.length];
		for (int i = 0; i < source.length; i++) {
			destin[i] = source[i];
		}
		return destin;
	}

	/**
	 * convert Long array to long array
	 * 
	 * @param source
	 * @return
	 */
	public static long[] transformLongArray(Long[] source) {
		long[] destin = new long[source.length];
		for (int i = 0; i < source.length; i++) {
			destin[i] = source[i];
		}
		return destin;
	}

	/**
	 * convert int array to Integer array
	 * 
	 * @param source
	 * @return
	 */
	public static Integer[] transformIntArray(int[] source) {
		Integer[] destin = new Integer[source.length];
		for (int i = 0; i < source.length; i++) {
			destin[i] = source[i];
		}
		return destin;
	}

	/**
	 * convert Integer array to int array
	 * 
	 * @param source
	 * @return
	 */
	public static int[] transformIntArray(Integer[] source) {
		int[] destin = new int[source.length];
		for (int i = 0; i < source.length; i++) {
			destin[i] = source[i];
		}
		return destin;
	}

	/**
	 * compare two object
	 * <ul>
	 * <strong>About result</strong>
	 * <li>if v1 > v2, return 1</li>
	 * <li>if v1 = v2, return 0</li>
	 * <li>if v1 < v2, return -1</li>
	 * </ul>
	 * <ul>
	 * <strong>About rule</strong>
	 * <li>if v1 is null, v2 is null, then return 0</li>
	 * <li>if v1 is null, v2 is not null, then return -1</li>
	 * <li>if v1 is not null, v2 is null, then return 1</li>
	 * <li>return v1.{@link Comparable#compareTo(Object)}</li>
	 * </ul>
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <V> int compare(V v1, V v2) {
		return v1 == null ? (v2 == null ? 0 : -1) : (v2 == null ? 1 : ((Comparable) v1).compareTo(v2));
	}

	/**
	 * 字符串转换成序列化对象
	 * 
	 * @Title: valueOf
	 * @Description: TODO(Describe the action of this method)
	 * @param :@param objStr
	 * @param :@return 参数
	 * @return :Object 返回类型
	 * @throws :@param objStr
	 * @throws :@return 异常
	 */
	public static Object valueOf(String objStr) {
		Object obj = null;
		if (objStr != null) {
			if (!objStr.equals("")) {
				byte[] mobileBytes = Base64.decode(objStr.toString().getBytes(), Base64.DEFAULT);
				ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
				ObjectInputStream objectInputStream;
				try {
					objectInputStream = new ObjectInputStream(byteArrayInputStream);
					obj = objectInputStream.readObject();
				} catch (StreamCorruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return obj;
	}

	/**
	 * 序列化对象copy一个新的对象
	* @Title: copyObject 
	* @Description: TODO(Describe the action of this method) 
	* @param :@param obj
	* @param :@return    参数 
	* @return :Object    返回类型 
	* @throws :@param obj
	* @throws :@return    异常
	 */
	public static Object copyObject(Object obj) {
		if (obj == null) {
			return null;
		}
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bout);
			out.writeObject(obj);
			out.close();
			ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
			ObjectInputStream in = new ObjectInputStream(bin);
			Object ret = in.readObject();
			in.close();
			return ret;
		} catch (Exception e) {
			return null;
		}
	}
}
