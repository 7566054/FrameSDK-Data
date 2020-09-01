package com.supoin.framesdk.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;


import com.nineoldandroids.view.ViewHelper;
import com.supoin.framesdk.R;
import com.supoin.framesdk.utils.ScreenUtils;

public class SlidingMenu extends HorizontalScrollView {
	/**
	 * 屏幕宽
	 */
	private int mScreenWidth;
	/**
	 * dp
	 */
	private int mMenuRightPadding;
	/**
	 * 菜单栏宽
	 */
	private int mMenuWidth;
	private int mHalfMenuWidth;

	private boolean isOpen;

	private boolean once;// 只需设置一次

	private ViewGroup mMenu;
	private ViewGroup mContent;

	private int scrollXUp = 0;

	private int mL = 0;// 0-菜单的宽度

	private float xDown, yDown, xUp, yUp, xMove, yMove;

	/**
	 * 用于计算手指滑动的速度。
	 */
	private VelocityTracker mVelocityTracker;

	private OnSlidingMenuIntent mOnSlidingMenuIntent;

	public SlidingMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);

	}

	public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mScreenWidth = ScreenUtils.getScreenWidth(context);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SlidingMenu, defStyle, 0);
		int n = a.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = a.getIndex(i);
			if (attr == R.styleable.SlidingMenu_rightPadding)
			{
				// 50
				mMenuRightPadding = a.getDimensionPixelSize(attr,
						(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2000f, getResources().getDisplayMetrics()));//
			}
		}
		a.recycle();
	}

	public SlidingMenu(Context context) {
		this(context, null, 0);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		/**
		 * 
		 */
		if (!once) {
			LinearLayout wrapper = (LinearLayout) getChildAt(0);
			mMenu = (ViewGroup) wrapper.getChildAt(0);
			mContent = (ViewGroup) wrapper.getChildAt(1);

			mMenuWidth = mScreenWidth * 3 / 4;
			mHalfMenuWidth = mMenuWidth / 2;// 打开或者关闭的临界点
			mMenu.getLayoutParams().width = mMenuWidth;
			mContent.getLayoutParams().width = mScreenWidth;

		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			//
			this.scrollTo(mMenuWidth, 0);
			once = true;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		createVelocityTracker(ev);

		int action = ev.getAction();

		switch (action) {

		case MotionEvent.ACTION_UP:
			scrollXUp = getScrollX();

			if (scrollXUp > mHalfMenuWidth) {
				if (getScrollVelocity() > 500 && mL < mMenuWidth - 60) {
					this.smoothScrollTo(0, 0);
					setOpen(true);
				} else {
					this.smoothScrollTo(mMenuWidth, 0);// 归位
					setOpen(false);
				}

			} else {
				if (getScrollVelocity() < -500 && mL > 60) {
					this.smoothScrollTo(mMenuWidth, 0);
					setOpen(false);
				} else {
					this.smoothScrollTo(0, 0);
					setOpen(true);
				}

			}

			recycleVelocityTracker();

			return true;
		}

		return super.onTouchEvent(ev);
	}

	/**
	 * 创建VelocityTracker对象，并将触摸content界面的滑动事件加入到VelocityTracker当中。
	 * 
	 * @param event
	 *            content界面的滑动事件
	 */
	private void createVelocityTracker(MotionEvent event) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
	}

	/**
	 * 回收VelocityTracker对象。
	 */
	private void recycleVelocityTracker() {
		mVelocityTracker.recycle();
		mVelocityTracker = null;
	}

	/**
	 * 获取手指在content界面滑动的速度。
	 * 
	 * @return 滑动速度，以每秒钟移动了多少像素值为单位。
	 */
	private int getScrollVelocity() {
		mVelocityTracker.computeCurrentVelocity(1000);
		int velocity = (int) mVelocityTracker.getXVelocity();
		return velocity;
	}

	/**
	 * 
	 */
	public void openMenu() {
		if (isOpen())
			return;
		this.smoothScrollTo(0, 0);
		setOpen(true);
	}

	/**
	 * 
	 */
	public void closeMenu() {
		if (isOpen()) {
			this.smoothScrollTo(mMenuWidth, 0);
			setOpen(false);
		}
	}

	/**
	 * 
	 */
	public void toggle() {
		if (isOpen()) {
			closeMenu();
		} else {
			openMenu();
		}
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		float scale = l * 1.0f / mMenuWidth;

		mL = l;
		// float leftScale = 1 - 0.3f * scale;
		// float rightScale = 0.8f + scale * 0.2f;

		// ViewHelper.setScaleX(mMenu, leftScale);
		// ViewHelper.setScaleY(mMenu, leftScale);
		// ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
		ViewHelper.setTranslationX(mMenu, mMenuWidth * scale * 0.7f);

		// ViewHelper.setPivotX(mContent, 0);
		// ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
		// ViewHelper.setScaleX(mContent, rightScale);
		// ViewHelper.setScaleY(mContent, rightScale);

		if (mOnSlidingMenuIntent != null) {
			mOnSlidingMenuIntent.onScrollChanged(l, t, oldl, oldt);
		}

	}

	public boolean isOpen() {
		return isOpen;
	}

	private void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
		mOnSlidingMenuIntent.isOpen(isOpen);
	}

	/**
	 * 
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 手指按下时，记录按下时的横坐标
			xDown = event.getRawX();
			yDown = event.getRawY();

			break;
		case MotionEvent.ACTION_MOVE:
			// 手指移动时，对比按下时的横坐标，计算出移动的距离
			xMove = event.getRawX();
			yMove = event.getRawY();

			//
			if (Math.abs(xMove - xDown) < ScreenUtils.dpToPx(getContext(), 80) && Math.abs(yMove - yDown) > ScreenUtils.dpToPx(getContext(), 10)) {
				return false;
			}

			break;
		case MotionEvent.ACTION_UP:
			// 手指抬起时，进行判断当前手势的意图，从而决定是滚动到menu界面，还是滚动到content界面
			xUp = event.getRawX();

			break;
		}

		return super.onInterceptTouchEvent(event);
	}

	public void setOnSlidingMenuIntent(OnSlidingMenuIntent onSlidingMenuIntent) {
		mOnSlidingMenuIntent = onSlidingMenuIntent;
	}

	public interface OnSlidingMenuIntent {
		abstract void isOpen(boolean isOpen);

		abstract void onScrollChanged(int l, int t, int oldl, int oldt);
	}

}
