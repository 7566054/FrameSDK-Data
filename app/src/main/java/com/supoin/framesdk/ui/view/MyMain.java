package com.supoin.framesdk.ui.view;

import android.widget.LinearLayout;

import com.blankj.utilcode.util.ActivityUtils;
import com.supoin.framesdk.base.BaseMainActivity;

public class MyMain extends BaseMainActivity {
    @Override
    public void onJumptoActivity() {
        ActivityUtils.startActivity(MyScanMD.class);
    }

    @Override
    public void setStartBackImage(LinearLayout mLayContent) {

    }
}
