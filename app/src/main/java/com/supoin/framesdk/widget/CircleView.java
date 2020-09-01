package com.supoin.framesdk.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircleView extends View {

	private int mMaxProgress = 100;

	private int mProgress = 30;

	private final int mCircleLineStrokeWidth = 3;

	// 画圆所在的距形区域
	private final RectF mRectF;

	private final Paint mPaint;

	private final Context mContext;

	private String mTxtHint1;

	private String mTxtHint2;

	public CircleView(Context context) {
		super(context);

		mContext = context;
		mRectF = new RectF();
		mPaint = new Paint();
	}

	public CircleView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mContext = context;
		mRectF = new RectF();
		mPaint = new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = this.getWidth();
		int height = this.getHeight();

		if (width != height) {
			int min = Math.min(width, height);
			width = min;
			height = min;
		}

		// 设置画笔相关属性
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.rgb(0xe9, 0xe9, 0xe9));
		canvas.drawColor(Color.TRANSPARENT);
		mPaint.setStrokeWidth(mCircleLineStrokeWidth);
		mPaint.setStyle(Style.STROKE);
		// 位置
		mRectF.left = mCircleLineStrokeWidth / 2; // 左上角x
		mRectF.top = mCircleLineStrokeWidth / 2; // 左上角y
		mRectF.right = width - mCircleLineStrokeWidth / 2; // 左下角x
		mRectF.bottom = height - mCircleLineStrokeWidth / 2; // 右下角y

		// 绘制圆圈，进度条背景
		mPaint.setColor(Color.rgb(222, 74, 74));
		canvas.drawArc(mRectF, -90, ((float) mProgress / mMaxProgress) * 360, false, mPaint);

	}

	public int getMaxProgress() {
		return mMaxProgress;
	}

	public void setMaxProgress(int maxProgress) {
		this.mMaxProgress = maxProgress;
	}

	public void setProgress(int progress) {
		this.mProgress = progress;
		this.invalidate();
	}

	public void setProgressNotInUiThread(int progress) {
		this.mProgress = progress;
		this.postInvalidate();
	}

	public String getmTxtHint1() {
		return mTxtHint1;
	}

	public void setmTxtHint1(String mTxtHint1) {
		this.mTxtHint1 = mTxtHint1;
	}

	public String getmTxtHint2() {
		return mTxtHint2;
	}

	public void setmTxtHint2(String mTxtHint2) {
		this.mTxtHint2 = mTxtHint2;
	}
}
