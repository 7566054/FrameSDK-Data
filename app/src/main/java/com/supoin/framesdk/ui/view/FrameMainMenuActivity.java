package com.supoin.framesdk.ui.view;


import android.content.Intent;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.supoin.framesdk.base.BaseActivity;
import com.supoin.framesdk.R;
import com.supoin.framesdk.base.BaseApplication;
import com.supoin.framesdk.utils.IntegerUtils;
import com.supoin.framesdk.utils.ScreenUtils;
import com.supoin.framesdk.widget.SlidingMenu;

import java.io.IOException;

import okhttp3.Call;

/**
 * 封装主界面功能，包括页面初始化、APP在线升级
 * Created by zwei on 2019/9/1.
 */
public abstract class FrameMainMenuActivity extends BaseActivity {


    /**
     * 设置功能菜单的图标及名称，子类重写此方法
     */
    protected abstract void loadUI();
    protected abstract void releaseUI();

    protected SlidingMenu sm_main;
    private RelativeLayout rl_main_bg;
    protected LinearLayout ll_homepage_main;
    protected RelativeLayout ll_homepage_menu;
    private int mScreenWidth = 0;
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;

    /**
     * @return 布局资源文件id
     */
    @Override
    public int getLayoutId() {
        return R.layout.activity_frame_main_menu;
    }

    /**
     * 页面初始化操作
     */
    @Override
    protected void initView() {
        setTileText("销邦系统");
        sm_main = findViewById(R.id.sm_main);
        ll_homepage_main = findViewById(R.id.ll_homepage_main);
        ll_homepage_menu = findViewById(R.id.ll_homepage_menu);
        rl_main_bg = findViewById(R.id.rl_main_bg);
        initMenu();
        loadUI();
    }

    private void initMenu() {

        mScreenWidth = ScreenUtils.getScreenWidth(getApplicationContext());

        if (sm_main.isOpen()) {
            sm_main.toggle();
        }

        sm_main.setOnSlidingMenuIntent(new SlidingMenu.OnSlidingMenuIntent() {

            @Override
            public void isOpen(boolean isOpen) {
                if (!isOpen) {
                    rl_main_bg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {

                int l_new = ((mScreenWidth * 3 / 4) - l) / 2;
                if (l_new < 0) {
                    l_new = 0;
                }
                if (l_new > 255) {
                    l_new = 255;
                }

                if (l_new > 6) {
                    rl_main_bg.setVisibility(View.VISIBLE);
                } else {
                    rl_main_bg.setVisibility(View.GONE);
                }

                String color = "#" + IntegerUtils.toHexColorStr(l_new / 6) + "000000";

                rl_main_bg.setBackgroundColor(Color.parseColor(color));

            }
        });

        rl_main_bg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sm_main.isOpen()) {
                    sm_main.toggle();
                }
            }
        });

    }

    /**
     * 检测APP在线升级
     */
    @Override
    protected void loadData() {

    }

    /**
     * 二次按返回键结束APP
     * @param keyCode key值
     * @param event 事件
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (KeyEvent.KEYCODE_BACK == keyCode) {

            if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
//                HomeProhibit();
                releaseUI();
                finish();
                BaseApplication.getInstance().finishAllActivity();
                System.exit(0);
            } else {
                Toast.makeText(getBaseContext(), "再点一次退出!",
                        Toast.LENGTH_SHORT).show();
                back_pressed = System.currentTimeMillis();
                return false;
            }

            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 使HOME键可用
     */
    public void HomeProhibit() {
        Intent intent2 = new Intent("com.geenk.action.HOMEKEY_SWITCH_STATE");
        intent2.putExtra("enable", true);
        getApplicationContext().sendBroadcast(intent2);
    }

    @Override
    protected void OnBack() {
        sm_main.toggle();
    }
}
