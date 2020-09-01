package com.supoin.framesdk.ui.view;

import com.supoin.framesdk.base.BaseApplication;
import com.supoin.framesdk.configure.FrameGlobalVariable;

public class MyApplication extends BaseApplication {
    @Override
    protected void initApplication() {
        //存储到内存卡的文件夹名称
        FrameGlobalVariable.projectName = "Test";
        //升级APK的文件名，注意不能带后缀.apk
        FrameGlobalVariable.appApkName = "cn.bojun";
        //更新版本号标志号
        FrameGlobalVariable.productNum = "AM001\\Test\\Test001";
    }

    @Override
    protected void initDB() {
    }
}
