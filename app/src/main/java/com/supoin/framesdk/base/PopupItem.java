package com.supoin.framesdk.base;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * 功能描述：弹窗内部子类项（绘制标题和图标）
 */
public class PopupItem {
	// 定义图片对象
	private Drawable mDrawable;
	// 定义文本对象
	private CharSequence mTitle;

	public PopupItem(Drawable drawable, CharSequence title) {
		this.setmDrawable(drawable);
		this.setmTitle(title);
	}

	public PopupItem(Context context, int titleId, int drawableId) {
		this.setmTitle(context.getResources().getText(titleId));
		this.setmDrawable(context.getResources().getDrawable(drawableId));
	}

	public PopupItem(Context context, CharSequence title, int drawableId) {
		this.setmTitle(title);
		this.setmDrawable(context.getResources().getDrawable(drawableId));
	}

	public PopupItem(Context context, CharSequence title) {
		this.setmTitle(title);
		this.setmDrawable(null);
	}

	public CharSequence getmTitle() {
		return mTitle;
	}

	public void setmTitle(CharSequence mTitle) {
		this.mTitle = mTitle;
	}

	public Drawable getmDrawable() {
		return mDrawable;
	}

	public void setmDrawable(Drawable mDrawable) {
		this.mDrawable = mDrawable;
	}
}
