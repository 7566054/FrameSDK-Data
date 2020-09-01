package com.supoin.framesdk.ui.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.viewpager.widget.ViewPager;

/**
 * @Author 安仔夏天勤奋
 * Create Date is  2019/9/6
 * Des
 */
public class ZoomImageView extends AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener, ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {
    private float mInitScale;
    private float mMaxScale;
    private float mMidScale;
    private Matrix mScaleMatrix;
    private boolean once = false;
    private ScaleGestureDetector mScaleGestureDetector;
    private int mLastPointCount;
    private boolean isCanDrag;
    private float mLatX;
    private float mLastY;
    private int mTouchSlop;
    private boolean isCheckTopAndBottom;
    private boolean isCheckLeftAndRight;
    private GestureDetector mGestureDetector;
    private boolean isAutoScaling;

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化Matrix
        mScaleMatrix = new Matrix();
        //预防在布局里没有或者设置其他类型
        super.setScaleType(ScaleType.MATRIX);
        //缩放初始化
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        //同样,缩放的捕获要建立在setOnTouchListener上
        setOnTouchListener(this);
        //符合滑动的距离
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        //自动缩放时需要有一个自动的过程
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                //如果再自动缩放中就不向下执行,防止多次双击
                if (isAutoScaling) {
                    return true;
                }
                //缩放的中心点
                float x = e.getX();
                float y = e.getY();
                if (getScale() < mMidScale) {
                    isAutoScaling = true;
                    postDelayed(new AutoScaleRunble(mMidScale, x, y), 16);
                } else {
                    isAutoScaling = true;
                    postDelayed(new AutoScaleRunble(mInitScale, x, y), 16);
                }
                return true;
            }

        });
    }

    private class AutoScaleRunble implements Runnable {
        private float mTrgetScale;
        private float x;
        private float y;
        private float tempScale;
        private float BIGGER = 1.07f;
        private float SMALLER = 0.93f;

        //构造传入缩放目标值,缩放的中心点
        public AutoScaleRunble(float mTrgetScale, float x, float y) {
            this.mTrgetScale = mTrgetScale;
            this.x = x;
            this.y = y;
            if (getScale() < mTrgetScale) {
                tempScale = BIGGER;
            }
            if (getScale() > mTrgetScale) {
                tempScale = SMALLER;
            }
        }

        @Override
        public void run() {
            mScaleMatrix.postScale(tempScale, tempScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
            float currentScale = getScale();
            //如果你想放大并且当然值并没有到达目标值,可以继续放大,同理缩小也是一样
            if ((tempScale > 1.0f && currentScale < mTrgetScale) || (tempScale < 1.0f && currentScale > mTrgetScale)) {
                postDelayed(this, 16);
            } else {//此时不能再进行放大或者缩小了,要放大为目标值
                float scale = mTrgetScale / currentScale;
                mScaleMatrix.postScale(scale, scale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);
                isAutoScaling = false;
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        //布尔值防止多次加载
        if (!once) {
            //获取屏幕的宽高
            int width = getWidth();
            int height = getHeight();
            //获取加载到的图片资源
            Drawable drawable = getDrawable();
            //获取图片的宽高
            int dWidth = drawable.getIntrinsicWidth();
            int dHeight = drawable.getIntrinsicHeight();
            //初始化的时候,我们要将图片居中显示
            //缩放比例
            float scale = 1.0f;
            if (dWidth > width && dHeight < height) {
                scale = width * 1.0f / dWidth;
            }
            if (dHeight > height && dWidth < width) {
                scale = height * 1.0f / dHeight;
            }
            if ((dWidth > width && dHeight > height) || (dWidth < width && dHeight < height)) {
                scale = Math.min(width * 1.0f / dWidth, height * 1.0f / dHeight);
            }
            //初始化缩放比例
            mInitScale = scale;
            //最大缩放比例
            mMaxScale = mInitScale * 4;
            //中等缩放比例
            mMidScale = mInitScale * 2;
            //图片移动到中心的距离
            int dx = getWidth() / 2 - dWidth / 2;
            int dy = getHeight() / 2 - dHeight / 2;
            //进行平移
            mScaleMatrix.postTranslate(dx, dy);
            //进行缩放
            mScaleMatrix.postScale(mInitScale, mInitScale, width / 2, height / 2);
            setImageMatrix(mScaleMatrix);
            once = true;
        }
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scaleFactor = detector.getScaleFactor();
        float scale = getScale();
        if (getDrawable() == null) {
            return true;
        }
        //这里是想放大和缩小
        if ((scale < mMaxScale && scaleFactor > 1.0f) || (scale > mInitScale && scaleFactor < 1.0f)) {
            //这里如果要缩放的值比初始化还要小的话,就按照最小可以缩放的值进行缩放
            if (scale * scaleFactor < mInitScale) {
                scaleFactor = mInitScale / scale;
            }
            //这个是放大的同理
            if (scale * scaleFactor > mMaxScale) {
                scaleFactor = mMaxScale / scale;
            }
            //detector.getFocusX(), detector.getFocusY(),是在缩放中心点进行缩放
            mScaleMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            //在缩放的时候会出现图片漏出白边,位置出现移动,所以要另外做移动处理
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        //开始时设置为true
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    private void checkBorderAndCenterWhenScale() {
        RectF matrixRectF = getMatrixRectF();
        float deltaX = 0;
        float deltY = 0;
        int width = getWidth();
        int height = getHeight();
        //缩放后的宽度大于屏幕
        if (matrixRectF.width() >= width) {

            if (matrixRectF.left > 0) {
                //这就是说左边露出了一部分,怎么办,补上啊,补多少?
                deltaX = -matrixRectF.left;
            }
            if (matrixRectF.right < width) {
                //这就是右边露出了
                deltaX = width - matrixRectF.right;
            }
        }
        if (matrixRectF.height() >= height) {
            if (matrixRectF.top > 0) {
                deltY = -matrixRectF.top;
            }
            if (matrixRectF.bottom < height) {
                deltY = -height - matrixRectF.bottom;
            }
        }
        //如果宽或者是高,小于屏幕的话,那就没理由的居中就行
        if (matrixRectF.width() < width) {
            deltaX = width / 2f - matrixRectF.right + matrixRectF.width() / 2;
        }
        if (matrixRectF.height() < height) {
            deltY = height / 2f - matrixRectF.bottom + matrixRectF.height() / 2;
        }
        mScaleMatrix.postTranslate(deltaX, deltY);
    }

    /**
     * 获取缩放
     *
     * @return
     */
    private float getScale() {
        float[] values = new float[9];
        mScaleMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    /**
     * 获取图片的RectF
     *
     * @return
     */
    private RectF getMatrixRectF() {
        //这里的matrix,记录了图片进行多指缩放后的缩放比例
        Matrix matrix = mScaleMatrix;
        RectF rectF = new RectF();
        Drawable drawable = getDrawable();
        if (null != drawable) {
            //设置原图片,大小位置
            rectF.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            //将原图片放入,然后进行缩放之后就得到了,缩放后的rectF
            /***
             * mapRect 是Matrix的其中一个方法,举个简单小例子
             RectF rect = new RectF(400, 400, 1000, 800);
             Matrix matrix = new Matrix();
             matrix.setScale(0.5f, 1f);
             Log.i(TAG, "mapRect: "+rect.toString());
             Log.i(TAG, "mapRect: "+rect.toString());
             mapRect: RectF(400.0, 400.0, 1000.0, 800.0)
             mapRect: RectF(200.0, 400.0, 500.0, 800.0)
             */
            matrix.mapRect(rectF);
        }
        return rectF;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //双击进行关联
        if (mGestureDetector.onTouchEvent(event)) {
            //如果是双击的话就直接不向下执行了
            return true;
        }
        //缩放进行关联
        mScaleGestureDetector.onTouchEvent(event);
        float x = 0;
        float y = 0;
        //可能出现多手指的情况
        int pointerCount = event.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x /= pointerCount;
        y /= pointerCount;
        if (mLastPointCount != pointerCount) {
            //手指变化后就不能继续拖拽
            isCanDrag = false;
            //记录最后的位置,重置
            mLatX = x;
            mLastY = y;
        }
        //记录最后一次手指的个数
        mLastPointCount = pointerCount;
        RectF rectF = getMatrixRectF();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //当图片放大时,这个时候左右滑动查看图片,就请求ViewPager不拦截事件!
                if (rectF.width() > getWidth() + 0.01 || rectF.height() > getHeight()) {
                    if (getParent() instanceof ViewPager) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //x,y移动的距离
                float dx = x - mLatX;
                float dy = y - mLastY;
                //这里的处理是,当图片移动到最边缘的时候,不能在移动了,此时是应该Viewpager去处理事件,翻页
                if ((dx < 0 && rectF.right <= getWidth()) || (dx > 0 && rectF.left >= 0)) {
                    if (getParent() instanceof ViewPager) {
                        //让父类进行拦截处理
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                } else if (rectF.width() > getWidth() + 0.01 || rectF.height() > getHeight()){
                    if (getParent() instanceof ViewPager) {
                        //让父类进行拦截处理
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                //如果是不能拖拽,可能是因为手指变化,这时就去重新检测看看是不是符合滑动
                if (!isCanDrag) {
                    //反正是根据勾股定理,调用系统API
                    isCanDrag = isMoveAction(dx, dy);
                }
                if (isCanDrag) {
                    if (getDrawable() != null) {
                        //判断是宽或者高小于屏幕,就不在那个方向进行拖拽
                        isCheckLeftAndRight = isCheckTopAndBottom = true;
                        if (rectF.width() < getWidth()) {
                            isCheckLeftAndRight = false;
                            dx = 0;
                        }
                        if (rectF.height() < getHeight()) {
                            isCheckTopAndBottom = false;
                            dy = 0;
                        }
                        mScaleMatrix.postTranslate(dx, dy);
                        //拖拽的时候会露出一部分空白,要补上
                        checkBorderAndCenterWhenTranslate();
                        setImageMatrix(mScaleMatrix);
                    }
                }
                mLatX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastPointCount = 0;
                break;
        }
        return true;
    }

    private void checkBorderAndCenterWhenTranslate() {
        RectF rectF = getMatrixRectF();
        float deltax = 0;
        float deltay = 0;
        int width = getWidth();
        int height = getHeight();
        if (rectF.top > 0 && isCheckTopAndBottom) {
            deltay = -rectF.top;
        }
        if (rectF.bottom < height && isCheckTopAndBottom) {
            deltay = height - rectF.bottom;
        }
        if (rectF.left > 0 && isCheckLeftAndRight) {
            deltax = -rectF.left;
        }
        if (rectF.right < width && isCheckLeftAndRight) {
            deltax = width - rectF.right;
        }
        mScaleMatrix.postTranslate(deltax, deltay);
    }

    private boolean isMoveAction(float dx, float dy) {
        return Math.sqrt(dx * dx + dy * dy) > mTouchSlop;
    }
}
