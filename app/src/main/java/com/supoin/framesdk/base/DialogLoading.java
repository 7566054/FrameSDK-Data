package com.supoin.framesdk.base;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.supoin.framesdk.R;


/**
 * 
 * @author zhoucheng
 * 
 */
public class DialogLoading extends Dialog {

	private Activity activity;
	private TextView tv_loading_hint;
	private final String hintDefault = BaseApplication.getInstance().getString(R.string.asking_for);
	private String hint;

	public DialogLoading(Activity activity) {
		super(activity, R.style.DialogLoading);
		this.activity = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View v = activity.getLayoutInflater().inflate(R.layout.dialog_loading, null);
		tv_loading_hint = (TextView) v.findViewById(R.id.tv_loading_hint);
		tv_loading_hint.setText(hint);
		setCanceledOnTouchOutside(false);
		setContentView(v);
	}

	public void showLoading(boolean backCancel) {
		showLoading(hintDefault, backCancel);
	}

	public void showLoading(String hint, boolean backCancel) {
		showLoading(hint, backCancel, null);
	}

	public void showLoading(String hint, boolean backCancel, WindowManager.LayoutParams params) {
		if (hint == null || hint.equals("")) {
			hint = hintDefault;
		}
		this.hint = hint;
		if (tv_loading_hint != null) {
			tv_loading_hint.setText(hint);
		}
		if (!activity.isFinishing() && !isShowing()) {
			setCancelable(backCancel);
			if (params != null) {
				getWindow().setAttributes(params);
			}
			show();
		}
	}

	public void closeLoading() {
		if (isShowing()) {
			dismiss();
		}
	}

	public boolean isDead() {
		return activity.isFinishing();
	}

}
