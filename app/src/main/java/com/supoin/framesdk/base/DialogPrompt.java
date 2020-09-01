package com.supoin.framesdk.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.supoin.framesdk.R;

/**
 *
 * @Description: 包含提示文字、确定、取消的弹出框
 * @author zc
 * @date 2015年1月12日 下午4:21:51
 */
public class DialogPrompt extends DialogBase {

	private TextView tv_title;

	private TextView tv_message;

	private Button btn_ok;

	private Button btn_cancel;

	private String title;

	private String message;

	private Activity mActivity;

	private OnOKClickListener mOnOKClickListener;
	private OnCancelClickListener mOnCancelClickListener;

	public DialogPrompt(Activity activity, String title, String message) {
		super(activity);
		setTitle(title);
		setMessage(message);
		mActivity = activity;
		setCanceledOnTouchOutside(true);
	}

	public DialogPrompt(Activity activity, String title, String message, boolean isTouchOutside) {
		super(activity);
		setTitle(title);
		setMessage(message);
		mActivity = activity;
		setCanceledOnTouchOutside(isTouchOutside);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_prompt);

		tv_title = (TextView) findViewById(R.id.tv_dialog_syman_title);
		tv_message = (TextView) findViewById(R.id.tv_dialog_message);
		btn_cancel = (Button) findViewById(R.id.btn_dialog_syman_cancel);
		btn_ok = (Button) findViewById(R.id.btn_dialog_syman_ok);

		initData();
		initView();
	}

	public void initData() {
		tv_title.setText(title);
		tv_message.setText(message);
	}

	public void initView() {
		btn_ok.setVisibility(View.GONE);
		btn_cancel.setVisibility(View.GONE);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * 
	 * @Title: setOkButton
	 * @Description: 点击确定按钮
	 * @param :@param onOKClickListener
	 * @param :@return 参数
	 * @return :DialogCirclePayPrompt 返回类型
	 * @throws :@param onOKClickListener
	 * @throws :@return 异常
	 */
	public DialogPrompt setOkButton(String okContent, OnOKClickListener onOKClickListener) {

		if (mActivity != null && mActivity.isFinishing()) {
			return this;
		}

		if (!this.isShowing()) {
			this.show();
		}

		if (okContent != null && !okContent.equals("")) {
			btn_ok.setText(okContent);
		}else {
			return this;
		}

		btn_ok.setVisibility(View.VISIBLE);
		mOnOKClickListener = onOKClickListener;
		btn_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				if (mOnOKClickListener != null)
					mOnOKClickListener.oKClicked();
			}
		});
		return this;
	}

	/**
	 * 
	 * @Title: setCancelButton
	 * @Description:点击取消按钮
	 * @param :@param onCancelClickListener
	 * @param :@return 参数
	 * @return :DialogCirclePayPrompt 返回类型
	 * @throws :@param onCancelClickListener
	 * @throws :@return 异常
	 */
	public DialogPrompt setCancelButton(String cancelContent, OnCancelClickListener onCancelClickListener) {

		if (mActivity != null && mActivity.isFinishing()) {
			return this;
		}

		if (!this.isShowing()) {
			this.show();
		}

		if (cancelContent != null && !cancelContent.equals("")) {
			btn_cancel.setText(cancelContent);
		}else {
			return this;
		}

		btn_cancel.setVisibility(View.VISIBLE);
		mOnCancelClickListener = onCancelClickListener;
		btn_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				if (mOnCancelClickListener != null)
					mOnCancelClickListener.cancelClicked();
			}
		});
		return this;
	}

	public interface OnOKClickListener {
		abstract void oKClicked();
	}

	public interface OnCancelClickListener {
		abstract void cancelClicked();
	}

}
