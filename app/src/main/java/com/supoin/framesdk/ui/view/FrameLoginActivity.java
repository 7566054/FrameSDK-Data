package com.supoin.framesdk.ui.view;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
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
import com.supoin.framesdk.utils.PermissionsUtil;
import com.supoin.framesdk.utils.ScreenUtils;
import com.supoin.framesdk.utils.StringUtils;
import com.supoin.framesdk.widget.SlidingMenu;


import java.io.File;
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
    protected abstract void createOrExistsDir();//外部自定义文件创建

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

        //动态申请权限
        if (Build.VERSION.SDK_INT >= 26 && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            PermissionsUtil.verifyStoragePermissions(this);
        }
        else{
            //初始化文件
            initFile();
            //初始化 导出数据
            initExportData();
            //初始化 崩溃日志
            initCrash();
            //系统日志实始化
            initSystemLog();
            //初始化数据库
            initDatabase();
            //外部自定义文件创建
            createOrExistsDir();
        }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //初始化文件
                    initFile();
                    //初始化 导出数据
                    initExportData();
                    //初始化 崩溃日志
                    initCrash();
                    //系统日志实始化
                    initSystemLog();
                    //初始化数据库
                    initDatabase();
                    //外部自定义文件创建
                    createOrExistsDir();
                } else {
                    ToastUtils.showShort(R.string.on_permisson_read_write);
                    finish();
                    BaseApplication.getInstance().finishAllActivity();
                    System.exit(0);
                }
                break;
            default:
        }
    }

    /**
     * 初始化项目文件路径，更新文件路径
     */
    private void initFile(){
        //文件目录初始化
        File projectFile = new File(FrameGlobalVariable.projectPath);
        if (!projectFile.exists())
            projectFile.mkdirs();

        File updateFile = new File(FrameGlobalVariable.updatePath);
        if (!updateFile.exists())
            updateFile.mkdirs();

    }

    /**
     * 初始化FTP导出文件路径
     */
    private void initExportData(){
        File exportPath = new File(FrameGlobalVariable.exportPath);
        if (!exportPath.exists())
            exportPath.mkdirs();
    }

    /**
     * 数据库初始化，默认自定义的文件位置
     */
    private void initDatabase(){
        File dbPath = new File(FrameGlobalVariable.databasePath);
        if (!dbPath.exists())
            dbPath.mkdirs();
    }

    /**
     * 初始化异常日志
     */
    private void initCrash(){
        File crashPath = new File(FrameGlobalVariable.crashPath);
        if (!crashPath.exists())
            crashPath.mkdirs();
    }

    /**
     * 初始化系统日志
     */
    private void initSystemLog(){
        File logPath = new File(FrameGlobalVariable.logPath);
        if (!logPath.exists())
            logPath.mkdirs();
        LogUtils.Config config = LogUtils.getConfig();
        config.setLog2FileSwitch(true);
        config.setDir(FrameGlobalVariable.logPath);
        config.setFilePrefix("supoin");
        config.setLogSwitch(true);
        config.setConsoleSwitch(false);
        config.setSaveDays(15);
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
            ToastUtils.showShort(getString(R.string.network_is_wrong));
            return;
        }
        if (editUser.getText().toString().trim().length() == 0) {
            ToastUtils.showShort(getString(R.string.user_empty));
            return;
        }

        if (editPassword.getText().toString().trim().length() == 0) {
            ToastUtils.showShort(getString(R.string.password_empty));
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
//                HomeProhibit();
                finish();
                BaseApplication.getInstance().finishAllActivity();
                System.exit(0);
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.click_again_exit),
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
