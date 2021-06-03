package com.supoin.framesdk.ui.view;

import android.widget.ImageView;

import com.supoin.framesdk.base.DialogProgressData;
import com.supoin.framesdk.service.BluetoothService;


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

    @Override
    protected void createOrExistsDir() {

    }
}
