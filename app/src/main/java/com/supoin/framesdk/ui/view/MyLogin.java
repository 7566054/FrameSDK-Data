package com.supoin.framesdk.ui.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.blankj.utilcode.util.ActivityUtils;
import com.supoin.framesdk.base.BaseActivity;
import com.supoin.framesdk.base.DialogLoading;
import com.supoin.framesdk.base.DialogProgressData;
import com.supoin.framesdk.base.DialogPrompt;
import com.supoin.framesdk.service.BluetoothService;
import com.supoin.framesdk.service.PrintService;
import com.supoin.framesdk.utils.CreateThreadPool;
import com.supoin.framesdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class MyLogin extends FrameLoginActivity {

    DialogProgressData mLoading;

    @Override
    protected void onLogin(String userCode, String password) {

    }

    @Override
    protected void setLogoIcon(ImageView imageLogo) {

    }

    @Override
    protected void setBluePrint() {
        BluetoothService bluetoothService = new BluetoothService(this);
        bluetoothService.initBluetooth();
    }

    @Override
    protected void initData() {
        //设置用户名称
        editUser.setText("1111");
        editPassword.setText("2222");
    }
}
