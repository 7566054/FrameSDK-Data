package com.supoin.framesdk.ui.view;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.supoin.framesdk.R;
import com.supoin.framesdk.R2;
import com.supoin.framesdk.base.BaseActivity;
import com.supoin.framesdk.configure.FrameGlobalConst;

import butterknife.BindView;

/**
 * @Author 安仔夏天勤奋
 * Create Date is  2019/9/6
 * Des  查看大图片
 */
public class FrameBigImageActivity extends BaseActivity {

    @BindView(R2.id.iv_bigImage)
    ZoomImageView mIvBigImage;

    /**
     * @return 布局资源文件id
     */
    @Override
    public int getLayoutId() {
        return R.layout.activity_frame_big_image;
    }

    /**
     * 设置标题
     */
    @Override
    protected void initView() {
        setTileText("查看大图");
    }

    @Override
    protected void loadData() {
        Bitmap bitmap = getIntent().getParcelableExtra(FrameGlobalConst.KEY_BIGIMAGE);
        if (bitmap != null){
            ToastUtils.showShort("请传输需要放大的图片");
            return;
        }
        mIvBigImage.setImageBitmap(bitmap);
    }
}
