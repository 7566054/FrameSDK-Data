package com.supoin.framesdk.base;


import android.app.ActionBar;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.supoin.framesdk.R;
import com.supoin.framesdk.R2;
import com.supoin.framesdk.utils.CommUtil;
import com.supoin.framesdk.utils.CreateThreadPool;
import com.supoin.framesdk.utils.ScanUtil;
import com.supoin.framesdk.utils.SoundUtil;
import com.supoin.framesdk.widget.PullToRefreshListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zwei on 2019/9/1.
 * 扫描基础页面，集成扫描、Listview控件、页面初始化、设置标题、页面销毁
 */

public abstract class BaseScanActivity extends AppCompatActivity implements ScanUtil.ScanResultListerner {

    @BindView(R2.id.listview_data)
    protected PullToRefreshListView mListView;//List控件

    @BindView(R2.id.ll_bottom_qty)
    protected LinearLayout mLayBottomQty;//数量区域

    @BindView(R2.id.rl_custom)
    RelativeLayout mRlCustom;//自定义区域

    @BindView(R2.id.tv_title_1)
    protected TextView mTitle_1; //标题1

    @BindView(R2.id.tv_title)
    TextView mTopTitle; //顶部标题

    @BindView(R2.id.tv_value_1)
    protected TextView mValue_1; //标题1显示值

    @BindView(R2.id.tv_value_2)
    protected TextView mValue_2;//标题2显示值


    @BindView(R2.id.btn_1)
    protected Button mBtn_1; //按钮1，用于显示自定义按钮的显示内容（必须修改）

    @BindView(R2.id.btn_2)
    protected Button mBtn_2;//按钮2，用于显示自定义按钮的显示内容（必须修改）

    @BindView(R2.id.btn_3)
    protected Button mBtn_3;//按钮3，用于用户自定义按钮的显示内容（必须修改）

    @BindView(R2.id.tv_more)
    protected TextView tv_more;//更多按钮

    @BindView(R2.id.lin_layout2)
    protected LinearLayout lin_layout2;//第二个listview头部，如果设备tv_11到tv_15为可见，必须先设置lin_layout2为可见，lin_layout2默认是不可见的

    protected View viewCustom;//用户自定义控件视图
    //扫描工具
    protected ScanUtil scanOperate;

    protected DialogLoading mLoading;
    protected DialogSelect mDialogSelect;
    protected DialogText mDialogText;

    protected CreateThreadPool createThreadPool = new CreateThreadPool();


    protected abstract @LayoutRes int getCustomLayoutId();//扫描界面顶部的布局ID

    /**
     * 设置标题
     */
    protected abstract String getBillTypeName();

    /**
     * 页面初始化
     */
    protected abstract void initView();

    /**
     * 加载数据
     */
    protected abstract void loadData();

    /**
     * 条码扫描
     * @param barcode 条码
     */
    protected abstract void scan(String barcode);

    /**
     * 按钮1事件
     */
    protected abstract void onBtn_1();

    /**
     * 按钮2事件
     */
    protected abstract void onBtn_2();

    /**
     * 按钮3事件
     */
    protected abstract void onBtn_3();

    /**
     * 关闭操作
     */
    protected abstract void close();

    /**
     * 点击顶部栏的更多事件
     */
    protected abstract void onTitleMore();

    //关闭事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            close();
        }
        return super.onKeyDown(keyCode, event);
    }

    //region 页面初始化
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            BaseApplication.getInstance().addActivity(this);
            setContentView(R.layout.common_scan);
            ButterKnife.bind(this);

            int customLayoutId = getCustomLayoutId();
            if (customLayoutId != 0){
                viewCustom = getLayoutInflater().inflate(customLayoutId, null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
                mRlCustom.addView(viewCustom,-1, params);
            }

            //页面初始化，外部必须实现
            initView();
            //加载数据
            loadData();

            String billTypeName = getBillTypeName();
            if (billTypeName != null){
                mTopTitle.setText(billTypeName);
            }

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
        catch (Exception ex) {
            ToastUtils.showShort("加载页面出错！原因："+ex.getMessage());
        }
    }

    @OnClick(R2.id.iv_back)
    protected void OnBack(){
        finish();
    }

    @OnClick(R2.id.btn_1)
    void OnBtn1(){
        onBtn_1();
    }

    @OnClick(R2.id.btn_2)
    void OnBtn2(){
        onBtn_2();
    }

    @OnClick(R2.id.btn_3)
    void OnBtn3(){
        onBtn_3();
    }

    @OnClick(R2.id.tv_more)
    void OnMore(){
        onTitleMore();
    }

    @Override
    protected void onResume() {
        try{
            super.onResume();
            //初始化扫描
            scanOperate = new ScanUtil(this, this);
        }catch (Exception ex){
            ToastUtils.showShort(ex.getMessage());
        }
    }

    public void setHeaderTitle(int tv_id, int title, float weight)
    {
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT, weight);//均分水平

        TextView textView=(TextView)this.findViewById(tv_id);
        textView.setLayoutParams(params);
        textView.setText(getResources().getString(title));
        textView.setVisibility(View.VISIBLE);

        if (lin_layout2.getVisibility() == View.GONE){
            if(tv_id == R.id.tv_11 || tv_id == R.id.tv_12 || tv_id == R.id.tv_13 ||
                    tv_id == R.id.tv_14 || tv_id == R.id.tv_15){
                lin_layout2.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setHeaderTitle(int tv_id, String title, float weight)
    {
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT, weight);//均分水平

        TextView textView=(TextView)this.findViewById(tv_id);
        textView.setLayoutParams(params);
        textView.setText(title);
        textView.setVisibility(View.VISIBLE);

        if (lin_layout2.getVisibility() == View.GONE){
            if(tv_id == R.id.tv_11 || tv_id == R.id.tv_12 || tv_id == R.id.tv_13 ||
                    tv_id == R.id.tv_14 || tv_id == R.id.tv_15){
                lin_layout2.setVisibility(View.VISIBLE);
            }
        }
    }

    //页面暂停处理，主要释放扫描枪
    @Override
    protected void onPause() {
        try{
            scanOperate.onDestroy(this);
        } catch (Exception ex){
            ToastUtils.showShort(ex.getMessage());
        }
        super.onPause();
    }

    /**
     * 扫描条码后，会调用此方法 扫描条码处理
     */
    @Override
    public void scanResult(String result) {
        scan(result);
    }

    /**
     * 点击页面任何空白处隐藏软键盘
     * @param event 不需要处理
     * @return 无
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN  &&
                getCurrentFocus()!=null &&
                getCurrentFocus().getWindowToken()!=null) {
            if (KeyboardUtils.isSoftInputVisible(this)) {
                KeyboardUtils.hideSoftInput(this);
            }
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 页面销毁事件
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 防止窗口泄露
        dismissLoadingDialog();
        dismissDialogSelect();
        dismissDialogText();
        createThreadPool.closeThreadPool();// 线程池关闭 不在接受新线程
        SoundUtil.getInstance().releaseAllSound();
        // 结束Activity&从栈中移除该Activity
        BaseApplication.getInstance().finishActivity(this);
    }

    /**
     * 设置返回键是否可见
     * @param flag true 可见 false 不可见
     */
    protected void setBackKey(boolean flag) {
        ImageView iv_back = findViewById(R.id.iv_back);
        if (iv_back != null) {
            if (flag) {
                iv_back.setVisibility(View.VISIBLE);
            } else {
                iv_back.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void showDialogLoading(final String msg, final boolean backCancel) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mLoading == null || mLoading.isDead()) {
                    mLoading = new DialogLoading(BaseScanActivity.this);
                }
                mLoading.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                    }
                });
                mLoading.showLoading(msg, backCancel, null);
            }
        });
    }

    public void showDialogSelect(String title, String select0, String select1,
                                 DialogSelect.OnDialogSelectClick onDialogSelectClick){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mDialogSelect == null) {
                    mDialogSelect = new DialogSelect(BaseScanActivity.this, title, select0, select1);
                }
                mDialogSelect.setOnDialogSelectClick(onDialogSelectClick);
                mDialogSelect.show();
            }
        });
    }

    public void showDialogText(final String text, final DialogText.OnDialogTextClick onDialogTextClick) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mDialogText == null || mDialogText.isDead()) {
                    mDialogText = new DialogText(BaseScanActivity.this);
                }
                mDialogText.showText(text, onDialogTextClick);
            }
        });
    }

    public void dismissLoadingDialog() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mLoading != null && mLoading.isShowing()) {
                    mLoading.closeLoading();
                    mLoading = null;
                }
            }
        });
    }

    public void dismissDialogSelect() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mDialogSelect != null) {
                    mDialogSelect.dismiss();
                    mDialogSelect = null;
                }
            }
        });
    }

    public void dismissDialogText() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mDialogText != null) {
                    mDialogText.dismiss();
                    mDialogText = null;
                }
            }
        });
    }
}

