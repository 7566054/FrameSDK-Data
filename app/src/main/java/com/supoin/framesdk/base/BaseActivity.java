package com.supoin.framesdk.base;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.supoin.framesdk.R;
import com.supoin.framesdk.R2;
import com.supoin.framesdk.utils.CreateThreadPool;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 基础页面：页面初始化、设置标题、页面销毁
 * Created by zwei on 2019/9/1.
 */

public abstract class BaseActivity extends AppCompatActivity {

    public DialogLoading mLoading;
    public DialogSelect mDialogSelect;
    public DialogText mDialogText;
    protected CreateThreadPool createThreadPool = new CreateThreadPool();

    //布局ID
    public abstract @LayoutRes int getLayoutId();

    /**
     * 页面初始化
     */
    protected abstract void initView();

    /**
     * 加载数据
     */
    protected abstract void loadData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseApplication.getInstance().addActivity(this);
        int layoutID = getLayoutId();
        if(layoutID==0){
            ToastUtils.showShort("请设置布局资源文件id！");
            return;
        }
        setContentView(layoutID);
        ButterKnife.bind(this);

        //控件初始化
        initView();
        //加载数据
        loadData();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
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
        // 结束Activity&从栈中移除该Activity
        // 防止窗口泄露
        dismissLoadingDialog();
        dismissDialogSelect();
        dismissDialogText();
        createThreadPool.closeThreadPool();// 线程池关闭 不在接受新线程
        BaseApplication.getInstance().finishActivity(this);

    }


    /**
     * 设置顶部标题栏、返回事件、键盘事件
     * @param title 标题字符
     */
    public void setTileText(String title) {
        TextView tv_title = findViewById(R.id.tv_title);
        if (tv_title != null) {
            tv_title.setText(title);
        }

        ImageView iv_back = findViewById(R.id.iv_back);
        if(iv_back!=null){
            iv_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnBack();
                }
            });
        }
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

    @OnClick(R2.id.iv_back)
    protected void OnBack()
    {
        finish();
    }

    public void showDialogLoading(final String msg, final boolean backCancel) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mLoading == null || mLoading.isDead()) {
                    mLoading = new DialogLoading(BaseActivity.this);
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
                    mDialogSelect = new DialogSelect(BaseActivity.this, title, select0, select1);
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
                    mDialogText = new DialogText(BaseActivity.this);
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
