package com.supoin.framesdk.base;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import com.supoin.framesdk.R;


/**
 * 
* @ClassName: {@link DialogBase}
* @Description: 弹出框基类
* @author youxuanhui
* @date 2015年1月12日 下午4:24:38
*
 */
public class DialogBase extends Dialog {

	protected Activity mActivity;
	
	public DialogBase(Activity activity) {
		super(activity, R.style.DialogSelectOrText);
		setCancelable(false);
		this.mActivity = activity;
	}
	
	public DialogBase(Activity activity, int theme) {
		super(activity,theme);
		setCancelable(false);
		this.mActivity = activity;
	}
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	 public void show() {
		 if (mActivity != null && !mActivity.isFinishing() && !isShowing()) {
			super.show();
		}
	 }
	
	@Override
	public void dismiss(){
		if (mActivity != null &&!mActivity.isFinishing() && isShowing()) {
			super.dismiss();
        }
	}
	
	
}
