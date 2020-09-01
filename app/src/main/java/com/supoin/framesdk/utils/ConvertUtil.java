package com.supoin.framesdk.utils;

/**
 * @Author 安仔夏天勤奋
 * Create Date is  2019/9/2
 * Des  安全类型 转换
 */
public class ConvertUtil {


    /**
     * 类型转换函数 安全
     */
    public static final String convertToString(Object object,String defaultvalut){

        if(object==null || object.toString().trim().length()==0||
                "null".equals(object.toString().trim())){
            return defaultvalut;
        }

        try{
            return String.valueOf(object.toString().trim());
        }catch (Exception e){
            return defaultvalut;
        }
    }

    /**
     * 类型转换函数 安全
     */
    public static final float convertToFloat(Object object,float defaultvalut){

        if(object==null || object.toString().trim().length()==0||
                "null".equals(object.toString().trim())){
            return defaultvalut;
        }
        try{
            return Float.valueOf(object.toString().trim());
        }catch (Exception e){
            try{
                return Double.valueOf(object.toString().trim()).floatValue();
            }catch (Exception ex){
                return defaultvalut;
            }

        }
    }

    /**
     * 类型转换函数 安全
     */
    public static final double convertToDouble(Object object,double defaultvalut){

        if(object==null || object.toString().trim().length()==0||
                "null".equals(object.toString().trim())){
            return defaultvalut;
        }
        try{
            return Double.valueOf(object.toString().trim());
        }catch (Exception e){
            return defaultvalut;
        }
    }

    /**
     * 类型转换函数 安全
     */
    public static final int convertToInt(Object object,int defaultValue){
        if(object==null || object.toString().trim().length()==0||
                "null".equals(object.toString().trim())){
            return defaultValue;
        }
//        无符号整形（unsigned int）变量的取值范围为：0～4294967295 ；
//        而整形（int）变量的取值范围为：2147483648～2147483647 .
        try{
            return Integer.parseInt(object.toString().trim());
        }catch (Exception e){
            try{
                return Double.valueOf(object.toString().trim()).intValue();
            }catch (Exception e1){
                return defaultValue;
            }
        }

    }
    /**
     * 类型转换函数 安全
     */
    public static final Long convertToLong(Object object,Long defaultValue){
        if(object==null || object.toString().trim().length()==0||
                "null".equals(object.toString().trim())){
            return defaultValue;
        }
        //最大值是9223372036854775807。
        try{
            return Long.parseLong(object.toString().trim());
        }catch (Exception e){
            try{
                return Double.valueOf(object.toString().trim()).longValue();
            }catch (Exception e1){
                return defaultValue;
            }
        }
    }


}
