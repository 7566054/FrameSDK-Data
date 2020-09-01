package com.supoin.framesdk.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Base64;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.supoin.framesdk.configure.FrameGlobalVariable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * 常用工具类
 * Created by zwei on 2019/9/1.
 */
public class CommUtil {

    private static CommUtil utils;

    public static CommUtil getInstance() {
        if (utils == null)
            utils = new CommUtil();

        return utils;
    }

    private String mKey	= "Supoin88"; //   初始化向量（IV）

    /**
     * 判断String是否为Json字符串
     * @param json json字符串
     * @return
     */
    public boolean isJsonString(String json) {
        try {
            new JsonParser().parse(json);
            return true;
        } catch (JsonSyntaxException e) {
            return false;
        } catch (JsonParseException e) {
            return false;
        }
    }


    /**
     * 获取下载地址的文件名（截取/后的字符）
     * @param url 地址
     * @return
     */
    public String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }


    /**
     * @return 获取当前日期
     */
    public String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return sdf.format(new Date());
    }

    /**
     * @return 返回当前时间
     */
    public String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return sdf.format(new Date());
    }

    /**
     * @return 返回当前时间
     */
    public String getCurrentTime_MS() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);
        return sdf.format(new Date());
    }

    /**
     * @return 返回单据号格式
     */
    public String getBillNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        return sdf.format(new Date());
    }

    /**
     * @param str 原字符
     * @param strLength 补位长度
     * @return 返回原字符补位
     */
    public String getPadRight(String str, int strLength) {
        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                str = str + "0";//右补0
                strLen = str.length();
            }
        }
        return str;
    }

    /**
     * edittext关闭弹出软键盘
     * @param context activity
     * @param editText 编缉控件
     */
    //软键盘禁用功能
    public void closeKeyboard(Activity context, EditText editText){
        context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        try {
            Class<EditText> cls = EditText.class;
            Method setSoftInputShownOnFocus;
            setSoftInputShownOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            setSoftInputShownOnFocus.setAccessible(true);
            setSoftInputShownOnFocus.invoke(editText, false);
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
    }

    /**
     * 显示软键盘
     * @param activity activity
     */
    public void showSoftKeyboard(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void copyAssetsFile(Context context, String assetsFileName) {
        try
        {
            //先判断目录文件是否存在，存在则不要拷贝
            String filePath = FrameGlobalVariable.databasePath + assetsFileName;//数据库路径
            File dbFile = new File(filePath);
            if(dbFile.exists()) return; //文件已存在则退出

            InputStream myInput;
            FileOutputStream myOutput = new FileOutputStream(new File(filePath));
            myInput = context.getAssets().open(assetsFileName);
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while(length > 0)
            {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }

            myOutput.flush();
            myInput.close();
            myOutput.close();
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
    }

    /**
     * 获取注册码
     * @param str 设备序列号
     * @return
     */
    public String getRegNum(String str)
    {
        try
        {
            byte[] ByteArray =EncryptAsDoNet(str,mKey);
            String Ret = "";
            Ret = Ret + (ByteArray[14] % 10);
            Ret = Ret + (ByteArray[19] % 10);
            Ret = Ret + (ByteArray[17] % 10);
            Ret = Ret + (ByteArray[10] % 10);
            Ret = Ret + (ByteArray[21] % 10);

            Ret = Ret + (ByteArray[15] % 10);
            Ret = Ret + (ByteArray[20] % 10);
            Ret = Ret + (ByteArray[13] % 10);
            Ret = Ret + (ByteArray[18] % 10);
            Ret = Ret + (ByteArray[11] % 10);
            return  Ret;

        }
        catch (Exception e)
        {
            LogUtils.e(e.toString());
            return "";
        }
    }

    private byte[] EncryptAsDoNet(String message, String key)throws Exception {

        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        byte[] encryptbyte = cipher.doFinal(message.getBytes());
        return  Base64.encode(encryptbyte, Base64.DEFAULT);
    }


    /**
     * @return 获取内核版本的日期，主要针对使用扫描JAR包ReaderManager.jar的内核版本过老时会崩溃的问题，使用扫描JAR包内核版本日期必须大于2018年1月
     */
    public String getCoreDate() {
        try {
            Process process = Runtime.getRuntime().exec("cat /proc/version");

            // get the output line
            InputStream outs = process.getInputStream();
            InputStreamReader isrout = new InputStreamReader(outs);
            BufferedReader brout = new BufferedReader(isrout, 8 * 1024);

            StringBuilder result = new StringBuilder();
            String line;

            while ((line = brout.readLine()) != null) {
                result.append(line);
            }

            if (!result.toString().equals("")) {
                String Keyword = "SMP PREEMPT ";
                int index = result.indexOf(Keyword);
                line = result.substring(index + Keyword.length());

                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                //java.util.Date对象
                Date date = sdf.parse(line);
                if (date != null) {
                    //2009-09-16
                    return new SimpleDateFormat("yyyy-MM", Locale.CHINA).format(date);
                }
                else {
                    return "";
                }
            }
        } catch (Exception e) {
            LogUtils.e(e.toString());
            return "";
        }
        return "";
    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public String encryptMD5Hash(String s) throws Exception {

        if (s == null) {
            return null;
        }
        MessageDigest digest;
        StringBuffer hasHexString;

        digest = MessageDigest.getInstance("MD5");
        digest.update(s.getBytes(), 0, s.length());
        byte messageDigest[] = digest.digest();
        hasHexString = new StringBuffer();
        for (int i = 0; i < messageDigest.length; i++) {
            String hex = Integer.toHexString(0xFF & messageDigest[i]);
            if (hex.length() == 1)
                hasHexString.append('0');
            hasHexString.append(hex);
        }
        return hasHexString.toString();

    }

    /**
     * 获取网络连接状态
     */
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public String MD5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname))
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            else
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
        } catch (Exception exception) {
        }
        return resultString;
    }

    private String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));

        return resultSb.toString();
    }

    private String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    private final String hexDigits[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    /**
     * 字符串是否为空，为null
     */
    public boolean isEmpty(CharSequence str) {
        if (str == null || str.equals("null") || str == "" || str.equals("") || str.length() == 0)
            return true;
        else {
            return false;
        }
    }

    /**
     * 清除条码的所有空格
     * @param str
     * @return
     */
    public String clearPaces(String str){
        String value = "";
        if (!TextUtils.isEmpty(str)){
            value = str.replace(" ", "");
        }

        return value;
    }
}
