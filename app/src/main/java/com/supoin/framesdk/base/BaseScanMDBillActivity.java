package com.supoin.framesdk.base;


import android.app.ActionBar;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.supoin.framesdk.R;
import com.supoin.framesdk.R2;
import com.supoin.framesdk.ui.adapter.AdapterPager;
import com.supoin.framesdk.utils.CommUtil;
import com.supoin.framesdk.utils.CreateThreadPool;
import com.supoin.framesdk.utils.ScanUtil;
import com.supoin.framesdk.utils.SoundUtil;
import com.supoin.framesdk.widget.PullToRefreshListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zwei on 2019/9/1.
 * 扫描基础页面，集成扫描、Listview控件、页面初始化、设置标题、页面销毁
 */

public abstract class BaseScanMDBillActivity extends AppCompatActivity implements ScanUtil.ScanResultListerner {

    @BindView(R2.id.tv_billType)
    TextView tv_billType;

    @BindView(R2.id.tv_billno)
    TextView tv_billno;

    @BindView(R2.id.tv_detail)
    TextView tv_detail;

    @BindView(R2.id.vp_guider)
    ViewPager vp_guider;

    @BindView(R2.id.btn_1)
    protected Button mBtn_1;//按钮1，用于显示自定义按钮的显示内容（必须修改）

    @BindView(R2.id.btn_2)
    protected Button mBtn_2;//按钮2，用于显示自定义按钮的显示内容（必须修改）

    @BindView(R2.id.btn_3)
    protected Button mBtn_3;//按钮3，用于用户自定义按钮的显示内容（必须修改）

    @BindView(R2.id.tv_billStatus)
    protected TextView tv_billStatus;//单据状态按钮，一般不显示。单据做废或已提交才显示

    @BindView(R2.id.tv_more)
    protected TextView tv_more;//更多按钮

    @BindView(R2.id.ll_bottom_button)
    protected LinearLayout ll_bottom_button;//底层按钮框架

    protected PullToRefreshListView mListView = null;
    private ArrayList<View> mPageViews;
    private AdapterPager mAdapterPager;

    private View viewDetail;//扫描明细布局控件
    protected View viewCustomBill;//扫描单据布局控件
    protected View viewCustomScan;//属于自定义的，需要给外部调用
    protected boolean isCustomScanMATCH = false; //自己定义的扫描布局是否铺满整个屏幕


    //扫描工具
    protected ScanUtil scanOperate;

    protected DialogLoading mLoading;
    protected DialogSelect mDialogSelect;
    protected DialogText mDialogText;
    private String billTypeName = "";

    protected CreateThreadPool createThreadPool = new CreateThreadPool();

    protected abstract @LayoutRes int getCustomScanLayoutId();//得到自定义扫描明细的布局文件

    protected abstract @LayoutRes int getCustomBillLayoutId(); //得到自定义单据的布局文件

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
            int customScanLayoutId = getCustomScanLayoutId();
            int customBillLayoutId = getCustomBillLayoutId();
            if(customBillLayoutId==0){
                ToastUtils.showShort("请设置单据布局资源文件id！");
                return;
            }

            setContentView(R.layout.common_scan_md_bill);
            ButterKnife.bind(this);

            mPageViews = new ArrayList<>();
            viewCustomBill = getLayoutInflater().inflate(customBillLayoutId, null);
            mPageViews.add(viewCustomBill);

            viewDetail = getLayoutInflater().inflate(R.layout.common_scan_md_detail, null);
            mPageViews.add(viewDetail);

            mListView = viewDetail.findViewById(R.id.listview_data);

            if (customScanLayoutId != 0) {
                viewCustomScan = getLayoutInflater().inflate(customScanLayoutId, null);
            }

            // 同时加载2个页面
            vp_guider.setOffscreenPageLimit(1);
            mAdapterPager = new AdapterPager(mPageViews);
            vp_guider.setAdapter(mAdapterPager);
            vp_guider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    chargeTab(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            billTypeName = getBillTypeName();

            //页面初始化，外部必须实现
            initView();
            //加载数据
            loadData();

            //加载自定义的扫描控件。
            if (customScanLayoutId != 0) {
                RelativeLayout rl_scan_custom = viewDetail.findViewById(R.id.rl_scan_custom);
                LinearLayout.LayoutParams params;
                if (isCustomScanMATCH) {
                    params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
                } else {
                    params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
                }
                rl_scan_custom.addView(viewCustomScan, -1, params);
            }

            chargeTab(0);

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
        catch (Exception ex) {
            ToastUtils.showShort("加载页面出错！原因："+ex.getMessage());
        }
    }

    @OnClick(R2.id.tv_billno)
    void OnMainBill(){
        chargeTab(0);
    }

    @OnClick(R2.id.tv_detail)
    void OnDetail(){
        chargeTab(1);
    }

    @OnClick(R2.id.tv_more)
    void OnMore(){
        onTitleMore();
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

    protected void chargeTab(int tag) {

        if (tag == 0) {
            tv_billno.setBackgroundColor(getResources().getColor(R.color.white));
            tv_detail.setBackgroundColor(getResources().getColor(R.color.transparent));
            tv_billno.setTextColor(getResources().getColor(R.color.theme));
            tv_detail.setTextColor(getResources().getColor(R.color.white));
            vp_guider.setCurrentItem(tag);
            tv_billType.setText(billTypeName == null ? "" : billTypeName + "详情");
        } else {
            tv_billno.setBackgroundColor(getResources().getColor(R.color.transparent));
            tv_detail.setBackgroundColor(getResources().getColor(R.color.white));
            tv_billno.setTextColor(getResources().getColor(R.color.white));
            tv_detail.setTextColor(getResources().getColor(R.color.theme));
            vp_guider.setCurrentItem(tag);
            tv_billType.setText(billTypeName == null ? "" : billTypeName + "明细单");
        }

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

        TextView textView=(TextView)viewDetail.findViewById(tv_id);
        textView.setLayoutParams(params);
        textView.setText(getResources().getString(title));
        textView.setVisibility(View.VISIBLE);

        LinearLayout linearLayout2=(LinearLayout)viewDetail.findViewById(R.id.lin_layout2);
        if (linearLayout2.getVisibility() == View.GONE){
            if(tv_id == R.id.tv_11 || tv_id == R.id.tv_12 || tv_id == R.id.tv_13 ||
                    tv_id == R.id.tv_14 || tv_id == R.id.tv_15){
                linearLayout2.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setHeaderTitle(int tv_id, String title, float weight)
    {
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT, weight);//均分水平

        TextView textView=(TextView)viewDetail.findViewById(tv_id);
        textView.setLayoutParams(params);
        textView.setText(title);
        textView.setVisibility(View.VISIBLE);

        LinearLayout linearLayout2=(LinearLayout)viewDetail.findViewById(R.id.lin_layout2);
        if (linearLayout2.getVisibility() == View.GONE){
            if(tv_id == R.id.tv_11 || tv_id == R.id.tv_12 || tv_id == R.id.tv_13 ||
                    tv_id == R.id.tv_14 || tv_id == R.id.tv_15){
                linearLayout2.setVisibility(View.VISIBLE);
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
                    mLoading = new DialogLoading(BaseScanMDBillActivity.this);
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
                    mDialogSelect = new DialogSelect(BaseScanMDBillActivity.this, title, select0, select1);
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
                    mDialogText = new DialogText(BaseScanMDBillActivity.this);
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

