/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.supoin.framesdk.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.supoin.framesdk.R;
import com.supoin.framesdk.widget.PullToRefreshBase.Orientation;

@SuppressLint("ViewConstructor")
public abstract class LoadingLayout extends FrameLayout implements ILoadingLayout {

	static final String LOG_TAG = "PullToRefresh-LoadingLayout";

	static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();

	private RelativeLayout mInnerLayout;

	protected ImageView mHeaderImage;

	private boolean mUseIntrinsicAnimation;

	private TextView mHeaderText;
	private TextView mSubHeaderText;

	protected final Mode mMode;
	protected final Orientation mScrollDirection;

	private CharSequence mPullLabel;
	private CharSequence mRefreshingLabel;
	private CharSequence mReleaseLabel;

	private OnLoadingLayoutIntent mOnLoadingLayoutIntent;
	private CircleView mCircleView;

	public LoadingLayout(Context context, final Mode mode, final Orientation scrollDirection, TypedArray attrs) {
		super(context);
		mMode = mode;
		mScrollDirection = scrollDirection;

		// zc（可以移动控件的位置） 到时候修改这个就好了，可以把图片放到中间
		switch (scrollDirection) {
		case HORIZONTAL:
			LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header_horizontal, this);// zc
																									// 水平下拉界面
			break;
		case VERTICAL:
		default:
			LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header_vertical, this);// zc
																									// 垂直下拉界面
			break;
		}

	
		if (!isInEditMode()) {
			mInnerLayout = (RelativeLayout) findViewById(R.id.fl_inner);// 获取界面
			mHeaderText = (TextView) mInnerLayout.findViewById(R.id.pull_to_refresh_text);
			mSubHeaderText = (TextView) mInnerLayout.findViewById(R.id.pull_to_refresh_sub_text);
			mHeaderImage = (ImageView) mInnerLayout.findViewById(R.id.pull_to_refresh_image);
			mCircleView = (CircleView) mInnerLayout.findViewById(R.id.pull_to_refresh_image_l);
			FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mInnerLayout.getLayoutParams();

			switch (mode) { // zc 获取需要变化的字
			case PULL_FROM_END:
				lp.gravity = scrollDirection == Orientation.VERTICAL ? Gravity.TOP : Gravity.LEFT;

				// Load in labels
				String pullLabel = attrs.getString(R.styleable.PullToRefresh_pull_to_refresh_from_bottom_pull_label);
				if (pullLabel == null || pullLabel.trim().equals("")) {
					mPullLabel = context.getString(R.string.pull_to_refresh_from_bottom_pull_label);
				} else {
					mPullLabel = pullLabel;
				}

				String refreshingLabel = attrs.getString(R.styleable.PullToRefresh_pull_to_refresh_from_bottom_refreshing_label);
				if (refreshingLabel == null || refreshingLabel.trim().equals("")) {
					mRefreshingLabel = context.getString(R.string.pull_to_refresh_from_bottom_refreshing_label);
				} else {
					mRefreshingLabel = refreshingLabel;
				}

				String releaseLabel = attrs.getString(R.styleable.PullToRefresh_pull_to_refresh_from_bottom_release_label);
				if (releaseLabel == null || releaseLabel.trim().equals("")) {
					mReleaseLabel = context.getString(R.string.pull_to_refresh_from_bottom_release_label);
				} else {
					mReleaseLabel = releaseLabel;
				}

				break;

			case PULL_FROM_START:
			default:
				lp.gravity = scrollDirection == Orientation.VERTICAL ? Gravity.BOTTOM : Gravity.RIGHT;

				// Load in labels
				pullLabel = attrs.getString(R.styleable.PullToRefresh_pull_to_refresh_pull_label);
				if (pullLabel == null || pullLabel.trim().equals("")) {
					mPullLabel = context.getString(R.string.pull_to_refresh_pull_label);
				} else {
					mPullLabel = pullLabel;
				}

				refreshingLabel = attrs.getString(R.styleable.PullToRefresh_pull_to_refresh_refreshing_label);
				if (refreshingLabel == null || refreshingLabel.trim().equals("")) {
					mRefreshingLabel = context.getString(R.string.pull_to_refresh_refreshing_label);
				} else {
					mRefreshingLabel = refreshingLabel;
				}

				releaseLabel = attrs.getString(R.styleable.PullToRefresh_pull_to_refresh_release_label);
				if (releaseLabel == null || releaseLabel.trim().equals("")) {
					mReleaseLabel = context.getString(R.string.pull_to_refresh_release_label);
				} else {
					mReleaseLabel = releaseLabel;
				}

				break;
			}
		}

		if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderBackground)) {
			Drawable background = attrs.getDrawable(R.styleable.PullToRefresh_ptrHeaderBackground);
			if (null != background) {
				ViewCompat.setBackground(this, background);
			}
		}

		if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderTextAppearance)) {
			TypedValue styleID = new TypedValue();
			attrs.getValue(R.styleable.PullToRefresh_ptrHeaderTextAppearance, styleID);
			setTextAppearance(styleID.data);
		}
		if (attrs.hasValue(R.styleable.PullToRefresh_ptrSubHeaderTextAppearance)) {
			TypedValue styleID = new TypedValue();
			attrs.getValue(R.styleable.PullToRefresh_ptrSubHeaderTextAppearance, styleID);
			setSubTextAppearance(styleID.data);
		}

		// Text Color attrs need to be set after TextAppearance attrs
		if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderTextColor)) {
			ColorStateList colors = attrs.getColorStateList(R.styleable.PullToRefresh_ptrHeaderTextColor);
			if (null != colors) {
				setTextColor(colors);
			}
		}
		if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderSubTextColor)) {
			ColorStateList colors = attrs.getColorStateList(R.styleable.PullToRefresh_ptrHeaderSubTextColor);
			if (null != colors) {
				setSubTextColor(colors);
			}
		}

		// Try and get defined drawable from Attrs
		Drawable imageDrawable = null;
		if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawable)) {
			imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawable);
		}

		// Check Specific Drawable from Attrs, these overrite the generic
		// drawable attr above
		switch (mode) {
		case PULL_FROM_START:
		default:
			if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawableStart)) {
				imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawableStart);
			} else if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawableTop)) {
				Utils.warnDeprecation("ptrDrawableTop", "ptrDrawableStart");
				imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawableTop);
			}
			break;

		case PULL_FROM_END:
			if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawableEnd)) {
				imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawableEnd);
			} else if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawableBottom)) {
				Utils.warnDeprecation("ptrDrawableBottom", "ptrDrawableEnd");
				imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawableBottom);
			}
			break;
		}

		// If we don't have a user defined drawable, load the default
		if (null == imageDrawable) {
			imageDrawable = context.getResources().getDrawable(getDefaultDrawableResId());
		}

		// Set Drawable, and save width/height
		setLoadingDrawable(imageDrawable);

		reset();
	}

	public final void setHeight(int height) {
		ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) getLayoutParams();
		lp.height = height;
		requestLayout();
	}

	public final void setWidth(int width) {
		ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) getLayoutParams();
		lp.width = width;
		requestLayout();
	}

	public final int getContentSize() {
		switch (mScrollDirection) {
		case HORIZONTAL:
			return mInnerLayout.getWidth();
		case VERTICAL:
		default:
			return mInnerLayout.getHeight();
		}
	}

	public final void hideAllViews() {
		if (View.VISIBLE == mHeaderText.getVisibility()) {
			mHeaderText.setVisibility(View.INVISIBLE);
		}
		if (View.VISIBLE == mHeaderImage.getVisibility()) {
			mHeaderImage.setVisibility(View.INVISIBLE);
		}
		if (View.VISIBLE == mSubHeaderText.getVisibility()) {
			mSubHeaderText.setVisibility(View.INVISIBLE);
		}
	}

	public final void onPull(float scaleOfLayout) {
		onPullImpl(scaleOfLayout);// zc 下拉或者上拉中

		// 0-100
		int pr = (int) ((scaleOfLayout - 0.01) * 100);

		if ((scaleOfLayout - 0.01) < 1) {
//			Log.i(AppConfig.TAG, "pr:" + pr);
			mHeaderImage.setImageResource(R.drawable.preloader_2_00000);
		} else {
			if ((scaleOfLayout - 0.01) > 1) {
				mHeaderImage.setImageResource(R.drawable.animalist_crile);
			}
			mCircleView.setVisibility(View.INVISIBLE);
		}

		if (mOnLoadingLayoutIntent != null) {
			mOnLoadingLayoutIntent.onPull(scaleOfLayout);
		}
	}

	public final void pullToRefresh() {
		if (null != mHeaderText) {
			mHeaderText.setText(mPullLabel);
		}

		// Now call the callback
		pullToRefreshImpl();
	}

	public final void refreshing() {// zc 正在刷新
		if (null != mHeaderText) {
			mHeaderText.setText(mRefreshingLabel);
		}

		mCircleView.setVisibility(View.INVISIBLE);

		mHeaderImage.setImageResource(R.drawable.animalist_crile);

		if (mUseIntrinsicAnimation) {
			((AnimationDrawable) mHeaderImage.getDrawable()).start();
		} else {
			refreshingImpl();
		}
	}

	public final void releaseToRefresh() {// zc 放手刷新
		if (null != mHeaderText) {
			mHeaderText.setText(mReleaseLabel);
		}

		// Now call the callback
		releaseToRefreshImpl();
	}

	public final void reset() {// 回到原位

		if (mOnLoadingLayoutIntent != null) {
			mOnLoadingLayoutIntent.reset();
		}

		if (null != mHeaderText) {
			mHeaderText.setText(mPullLabel);
		}

		if (mUseIntrinsicAnimation) {
			try {
				((AnimationDrawable) mHeaderImage.getDrawable()).stop();
			} catch (Exception e) {
			}
		} else {
			// Now call the callback
			resetImpl();
		}

	}

	@Override
	public void setLastUpdatedLabel(CharSequence label) {
		setSubHeaderText(label);
	}

	public final void setLoadingDrawable(Drawable imageDrawable) {
		// Set Drawable
		if (!isInEditMode()) {
			mHeaderImage.setImageDrawable(imageDrawable);
		}

		mUseIntrinsicAnimation = (imageDrawable instanceof AnimationDrawable);

		// Now call the callback
		onLoadingDrawableSet(imageDrawable);
	}

	public void setPullLabel(CharSequence pullLabel) {
		mPullLabel = pullLabel;
	}

	public void setRefreshingLabel(CharSequence refreshingLabel) {
		mRefreshingLabel = refreshingLabel;
	}

	public void setReleaseLabel(CharSequence releaseLabel) {
		mReleaseLabel = releaseLabel;
	}

	@Override
	public void setTextTypeface(Typeface tf) {
		mHeaderText.setTypeface(tf);
	}

	public final void showInvisibleViews() {
		if (View.INVISIBLE == mHeaderText.getVisibility()) {
			mHeaderText.setVisibility(View.VISIBLE);
		}
		if (View.INVISIBLE == mHeaderImage.getVisibility()) {
			mHeaderImage.setVisibility(View.VISIBLE);
		}
		if (View.INVISIBLE == mSubHeaderText.getVisibility()) {
			mSubHeaderText.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Callbacks for derivative Layouts
	 */

	protected abstract int getDefaultDrawableResId();

	protected abstract void onLoadingDrawableSet(Drawable imageDrawable);

	/**
	 * 下拉中
	 * 
	 * @param scaleOfLayout
	 */
	protected abstract void onPullImpl(float scaleOfLayout);

	protected abstract void pullToRefreshImpl();//

	/**
	 * 刷新中
	 */
	protected abstract void refreshingImpl();// 刷新中

	/**
	 * 放手刷新
	 */
	protected abstract void releaseToRefreshImpl();// zc 放手刷新

	protected abstract void resetImpl();

	private void setSubHeaderText(CharSequence label) {
		if (null != mSubHeaderText) {
			if (TextUtils.isEmpty(label)) {
				// mSubHeaderText.setVisibility(View.GONE);
			} else {
				mSubHeaderText.setText(label);

				// Only set it to Visible if we're GONE, otherwise VISIBLE will
				// be set soon
				if (View.GONE == mSubHeaderText.getVisibility()) {
					mSubHeaderText.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	private void setSubTextAppearance(int value) {
		if (null != mSubHeaderText) {
			mSubHeaderText.setTextAppearance(getContext(), value);
		}
	}

	private void setSubTextColor(ColorStateList color) {
		if (null != mSubHeaderText) {
			mSubHeaderText.setTextColor(color);
		}
	}

	private void setTextAppearance(int value) {
		if (null != mHeaderText) {
			mHeaderText.setTextAppearance(getContext(), value);
		}
		if (null != mSubHeaderText) {
			mSubHeaderText.setTextAppearance(getContext(), value);
		}
	}

	private void setTextColor(ColorStateList color) {
		if (null != mHeaderText) {
			mHeaderText.setTextColor(color);
		}
		if (null != mSubHeaderText) {
			mSubHeaderText.setTextColor(color);
		}
	}

	public void setOnLoadingLayoutIntent(OnLoadingLayoutIntent onLoadingLayoutIntent) {
		mOnLoadingLayoutIntent = onLoadingLayoutIntent;
	}

	public interface OnLoadingLayoutIntent {

		abstract void onPull(float scaleOfLayout);

		abstract void reset();

	}

}
