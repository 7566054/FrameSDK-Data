package com.supoin.framesdk.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.LinearLayout;


import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SDCardUtils;
import com.supoin.framesdk.R;
import com.supoin.framesdk.configure.FrameGlobalConst;
import com.supoin.framesdk.db.FrameDbBLL;


public abstract class BaseMainActivity extends BaseActivity {

    private Handler handler;
    protected LinearLayout mLayContent;

    /**
     *  跳转到相应的指定页面（比如登录页面或主个界面），子类重写此方法
     */
    public abstract void onJumptoActivity();

    /**
     * 更改启动背景图片
     * @param mLayContent LinearLayout控件
     */
    public abstract void setStartBackImage(LinearLayout mLayContent);

    @Override
    public int getLayoutId() {
        return R.layout.activity_base_main;
    }

    /**
     * 页面初始化
     */
    @Override
    protected void initView() {
        mLayContent = findViewById(R.id.activity_main);
        setStartBackImage(mLayContent);
    }

    /**
     * 加载数据
     */
    @Override
    protected void loadData() {
        //禁止HOME键
        onHomeProhibit();
        if (!SDCardUtils.isSDCardEnableByEnvironment()) {

            showDialogText("未检测到手机内存卡", null);
            return;
        }
        startLogoTimer();
    }


    /**
     * 启动页处理，弹出注册页面，如果已注册，则跳转到用户指定页面
     */
    @SuppressLint("HandlerLeak")
    private void startLogoTimer(){
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == FrameGlobalConst.REQ_START_FINISH) {
                    onJumptoActivity();
                }
            }
        };
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(FrameGlobalConst.START_TIMEOUT);
                } catch (InterruptedException e) {
                    LogUtils.e(e.toString());
                }

                Message msg = new Message();
                msg.what = FrameGlobalConst.REQ_START_FINISH;
                handler.sendMessage(msg);
            }
        };
        new Thread(runnable).start();
    }



    /**
     * 按返回键结束页面
     * @param keyCode 键值
     * @param event 事件
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && KeyEvent.KEYCODE_BACK == keyCode) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 设置HOME键是否可用
     */
    protected void onHomeProhibit() {
        Intent intent2 = new Intent("com.geenk.action.HOMEKEY_SWITCH_STATE");
        intent2.putExtra("enable", false);
        getApplicationContext().sendBroadcast(intent2);
    }
}
