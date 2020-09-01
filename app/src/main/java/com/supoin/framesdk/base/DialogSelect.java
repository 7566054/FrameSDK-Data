package com.supoin.framesdk.base;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.supoin.framesdk.R;


public class DialogSelect extends AlertDialog {

	private Button btn_dialog_select1;
	private Button btn_dialog_select_cancel;
	private Button btn_dialog_select0;
	private TextView tv_dialog_title;
	private OnDialogSelectClick mOnDialogSelectClick;
	private String mSelect0;
	private String mSelect1;
	private String mTitle;
	private Activity mActivity;

	public DialogSelect(Activity activity, String title, String select0, String select1) {
		super(activity, R.style.DialogSelectOrText);
		mActivity = activity;
		mSelect0 = select0;
		mSelect1 = select1;
		mTitle = title;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_select);

		btn_dialog_select0 = (Button) findViewById(R.id.btn_dialog_select0);
		btn_dialog_select1 = (Button) findViewById(R.id.btn_dialog_select1);
		btn_dialog_select_cancel = (Button) findViewById(R.id.btn_dialog_select_cancel);
		tv_dialog_title = (TextView) findViewById(R.id.tv_dialog_title);

		initUI();
	}

	private void initUI() {

		Window window = this.getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.AnimBottom);
		window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		tv_dialog_title.setText(mTitle);

		if (isNull(mSelect0)) {
			btn_dialog_select0.setVisibility(View.GONE);
		} else {
			btn_dialog_select0.setText(mSelect0);
		}
		if (isNull(mSelect1)) {
			btn_dialog_select1.setVisibility(View.GONE);
		} else {
			btn_dialog_select1.setText(mSelect1);
		}

		btn_dialog_select0.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				mOnDialogSelectClick.onClick(0);
			}
		});

		btn_dialog_select1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				mOnDialogSelectClick.onClick(1);
			}
		});

		btn_dialog_select_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogSelect.this.dismiss();
			}
		});

	}

	private boolean isNull(String str) {
		return str == null || str.trim().equals("") || str.trim().equalsIgnoreCase("null");
	}

	@Override
	public void show() {
		if (!mActivity.isFinishing() && !isShowing()) {
			super.show();
		}
	}

	@Override
	public void dismiss() {
		if (isShowing()) {
			super.dismiss();
		}
	}

	public void setOnDialogSelectClick(OnDialogSelectClick onDialogSelectClick) {
		mOnDialogSelectClick = onDialogSelectClick;
	}

	public interface OnDialogSelectClick {
		void onClick(int which);
	}
}
