package com.supoin.framesdk.ui.view;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.supoin.framesdk.R;
import com.supoin.framesdk.R2;
import com.supoin.framesdk.base.BaseActivity;
import com.supoin.framesdk.base.BaseApplication;
import com.supoin.framesdk.configure.FrameGlobalConst;
import com.supoin.framesdk.configure.FrameGlobalVariable;
import com.supoin.framesdk.utils.GlobalUtil;
import com.supoin.framesdk.utils.IntegerUtils;
import com.supoin.framesdk.utils.ScreenUtils;
import com.supoin.framesdk.utils.StringUtils;
import com.supoin.framesdk.widget.SlidingMenu;


import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 框架登录功能，集成登录界面初始化、控件初始化等
 * Created by zwei on 2019/9/1.
 */
public abstract class FrameLoginActivity extends BaseActivity {

    protected abstract void onLogin(String userCode, String password);
    protected abstract void setLogoIcon(ImageView imageLogo);
    protected abstract void setBluePrint();
    protected abstract void initData();

    @BindView(R2.id.et_user_code)
    protected EditText editUser;
    @BindView(R2.id.et_pwd)
    protected EditText editPassword;
    @BindView(R2.id.iv_logo)
    protected ImageView imageLogo;
    @BindView(R2.id.rl_main_bg)
    RelativeLayout rl_main_bg;

    @BindView(R2.id.ll_homepage_menu)
    public RelativeLayout ll_homepage_menu;

    @BindView(R2.id.sm_main)
    SlidingMenu sm_main;

    private static final int TIME_DELAY = 2000;
    private static long back_pressed;
    private boolean isShowPass = false;
    private int mScreenWidth = 0;

    /**
     * @return 布局资源文件id
     */
    @Override
    public int getLayoutId() {
        return R.layout.activity_frame_login;
    }

    /**
     * 设置标题
     */
    @Override
    protected void initView() {

        setTileText("登录");
        initMenu();
    }

    /**
     * 页面初始化操作
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void loadData() {

        setLogoIcon(imageLogo);
        initData();
        setBluePrint();

        if (!editUser.getText().toString().equals(""))
            editPassword.requestFocus();

        //测试时设置默认密码
        editPassword.setSelection(editPassword.length());

        editPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())){
                    onClickLogin();
                }
                return false;
            }
        });

        editPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Drawable drawable = editPassword.getCompoundDrawables()[2];
                    boolean isTouchRight = event.getX() > (editPassword.getWidth() - editPassword.getTotalPaddingRight()) &&
                            (event.getX() < ((editPassword.getWidth() - editPassword.getPaddingRight())));

                    if (drawable != null && isTouchRight) {
                        isShowPass = !isShowPass;
                        editPassword.setInputType(isShowPass ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        editPassword.setCompoundDrawablesWithIntrinsicBounds(editPassword.getCompoundDrawables()[0], editPassword.getCompoundDrawables()[1],
                                isShowPass ? getResources().getDrawable(R.mipmap.ic_password_show) : getResources().getDrawable(R.mipmap.ic_password_hide), editPassword.getCompoundDrawables()[3]);

                        editPassword.setSelection(TextUtils.isEmpty(editPassword.getText()) ? 0 : editPassword.getText().length());
                    }
                }
                return false;
            }
        });
    }

    private void showHisName() {

        String temp0 = GlobalUtil.getInstance().getValue(this, FrameGlobalConst.KEY_HISTORY_NAME_0, "");
        String temp1 = GlobalUtil.getInstance().getValue(this, FrameGlobalConst.KEY_HISTORY_NAME_1, "");
        String temp2 = GlobalUtil.getInstance().getValue(this, FrameGlobalConst.KEY_HISTORY_NAME_2, "");

        List<String> hisNameList = new LinkedList<String>();
        if (!StringUtils.isNull(temp0)) {
            hisNameList.add(temp0);
        }
        if (!StringUtils.isNull(temp1)) {
            hisNameList.add(temp1);
        }
        if (!StringUtils.isNull(temp2)) {
            hisNameList.add(temp2);
        }

        if (hisNameList.size() <= 0) {
            ToastUtils.showShort(R.string.no_loginname_history);
            return;
        }

        final String[] items = new String[hisNameList.size()];
        for (int i = 0; i < hisNameList.size(); i++) {
            String hisName = hisNameList.get(i);
            items[i] = hisName;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.history_loginname);
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String hisName = items[which];
                editUser.setText(hisName);
                editUser.setSelection(editUser.getText().length());
            }
        });
        builder.create().show();
    }

    public void saveHisName(String name) {

        String temp0 = GlobalUtil.getInstance().getValue(this,  FrameGlobalConst.KEY_HISTORY_NAME_0, "null");
        String temp1 = GlobalUtil.getInstance().getValue(this,  FrameGlobalConst.KEY_HISTORY_NAME_1, "null");
        if ((!temp0.equals(name) && !temp1.equals(name))) {
            GlobalUtil.getInstance().saveValue(this, FrameGlobalConst.KEY_HISTORY_NAME_0, name);
            GlobalUtil.getInstance().saveValue(this, FrameGlobalConst.KEY_HISTORY_NAME_1, temp0);
            GlobalUtil.getInstance().saveValue(this, FrameGlobalConst.KEY_HISTORY_NAME_2, temp1);
        } else if (!temp0.equals(name) && temp1.equals(name)) {
            GlobalUtil.getInstance().saveValue(this, FrameGlobalConst.KEY_HISTORY_NAME_0, name);
            GlobalUtil.getInstance().saveValue(this, FrameGlobalConst.KEY_HISTORY_NAME_1, temp0);
        }
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

    @OnClick(R2.id.iv_user_his)
    void OnHisUser(){
        showHisName();
    }

    /**
     * 登录功能
     */
    @OnClick(R2.id.btn_login)
    public void onClickLogin(){

//        if (!Build.MANUFACTURER.equals("SUPOIN")){
//            ToastUtils.showShort("机器不合法，请使用销邦设备！");
//            return;
//        }
        if (!NetworkUtils.isConnected()){
            ToastUtils.showShort("您的网络有问题，请检查网络连接！");
            return;
        }
        if (editUser.getText().toString().trim().length() == 0) {
            ToastUtils.showShort("用户名不能为空");
            return;
        }

        if (editPassword.getText().toString().trim().length() == 0) {
            ToastUtils.showShort("用户名不能为空");
            return;
        }

        onLogin(editUser.getText().toString().trim(),editPassword.getText().toString().trim());
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
                HomeProhibit();
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
