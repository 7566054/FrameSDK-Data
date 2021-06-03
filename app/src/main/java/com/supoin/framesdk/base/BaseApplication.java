package com.supoin.framesdk.base;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.android.scanner.impl.ReaderManager;
import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.SDCardUtils;
import com.supoin.framesdk.configure.FrameGlobalVariable;
import com.supoin.framesdk.db.DBFrameHelper;
import com.supoin.framesdk.utils.CommUtil;

import java.io.File;
import java.util.Stack;


/**
 * 应用程序类
 * Created by zwei on 2019/9/1.
 */

public abstract class BaseApplication extends Application {

    private static Stack<Activity> activityStack;
    private static BaseApplication singleton;
    private ReaderManager mScannerManager;
    private int outPutMode;
    private int endCharMode;

    protected abstract void initApplication();

    protected abstract void initDB();


    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;

        try {
            initApplication();

            FrameGlobalVariable.projectPath = SDCardUtils.getSDCardPathByEnvironment() + "/" + FrameGlobalVariable.projectName;
            FrameGlobalVariable.updatePath = FrameGlobalVariable.projectPath + "/Update/";
            //数据库存储路径
            FrameGlobalVariable.databasePath = FrameGlobalVariable.projectPath + "/Database/";
            //异常崩溃路径
            FrameGlobalVariable.crashPath = FrameGlobalVariable.projectPath + "/Crash/";
            //系统日志路径
            FrameGlobalVariable.logPath = FrameGlobalVariable.projectPath + "/Log";
            //FTP导出路径
            FrameGlobalVariable.exportPath = FrameGlobalVariable.projectPath + "/ExportDataFile/";

            //App崩溃日志初始化
            CrashUtils.init(FrameGlobalVariable.crashPath, crashListener);

            //获取内核版本的日期,大于2018-01时才调用扫描JAR包，否则程序会崩溃
            String coreDate = CommUtil.getInstance().getCoreDate();
            if (coreDate.compareTo("2018-01") >= 0) {
                mScannerManager = ReaderManager.getInstance();
                initScanConfig();
            }

            //拷贝数据库到SD卡上
            CommUtil.getInstance().copyAssetsFile(this, "FrameSDK.db");

            //如果设备是android 10的，获取设备序列号要方式有改变，否则获取的序列号是为unknown
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                FrameGlobalVariable.DeviceID = CommUtil.getInstance().getProperty("vendor.gsm.serial", "");
                if (FrameGlobalVariable.DeviceID.equals("")) {
                    FrameGlobalVariable.DeviceID = CommUtil.getInstance().getProperty("ro.serialno", "");
                }

                if (FrameGlobalVariable.DeviceID.length() >= 30) {
                    FrameGlobalVariable.DeviceID = FrameGlobalVariable.DeviceID.substring(0, 30);
                }

            } else {
                FrameGlobalVariable.DeviceID = Build.SERIAL;
            }

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                FrameGlobalVariable.IMEI =Settings.System.getString( getContentResolver(), Settings.Secure.ANDROID_ID);
            }
            else{
                FrameGlobalVariable.IMEI = PhoneUtils.getIMEI();
            }

            FrameGlobalVariable.BrandName = Build.BRAND;
            FrameGlobalVariable.Manufacturer = Build.MANUFACTURER;
            FrameGlobalVariable.Model = Build.MODEL;

            FrameGlobalVariable.dbFrameHelper = new DBFrameHelper(this);
            initDB();
        }
        catch (Exception ex)
        {
            LogUtils.e(ex);
        }
    }

    /**
     * 初始化扫描枪的设置
     */
    private void initScanConfig() {
        try {
            if (mScannerManager != null){
                if (!mScannerManager.GetActive())
                    mScannerManager.SetActive(true);

                if (!mScannerManager.isEnableScankey())
                    mScannerManager.setEnableScankey(true);

                outPutMode = mScannerManager.getOutPutMode();
                if (outPutMode != 2)
                    mScannerManager.setOutPutMode(2);//2 不是api模式设置为api模式

                endCharMode = mScannerManager.getEndCharMode();
                if (endCharMode != 3)
                    mScannerManager.setEndCharMode(3); //结尾设置为null
            }
        }catch (Exception e) {
            LogUtils.e(e.toString());
        }
    }

    /**
     * 释放扫描设置
     */
    public void releaseScanConfig()
    {
        if (mScannerManager != null) {
            mScannerManager.setOutPutMode(outPutMode);//扫描模式设置回去
            mScannerManager.setEndCharMode(endCharMode);//结束字符设置回去

            mScannerManager.Release();
        }
    }

    /**
     * 异常崩溃事件处理
     */
    private CrashUtils.OnCrashListener crashListener = new CrashUtils.OnCrashListener() {
        @Override
        public void onCrash(String crashInfo, Throwable e) {
            try {
                new Thread() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                }.start();
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                LogUtils.e(ex.getMessage());
            }
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    };

    /**
     * 单例模式
     * @return 返回application
     */
    public static BaseApplication getInstance() {
        return singleton;
    }

    /**
     * add Activity 添加Activity到栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }

        activityStack.add(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        releaseScanConfig();
        activityStack.clear();
    }
}
