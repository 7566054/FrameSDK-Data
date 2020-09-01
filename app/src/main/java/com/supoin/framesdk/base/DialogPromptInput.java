package com.supoin.framesdk.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.supoin.framesdk.R;
import com.supoin.framesdk.utils.StringUtils;

/**
 *
 * @Description: 包含提示文字、确定、取消的弹出框
 * @author zwei
 * @date 2020年3月20日 下午4:21:51
 */
public class DialogPromptInput extends DialogBase {

	private TextView tv_dialog_title;
	private EditText et_dialog_message;
	private Button btn_ok;
	private Button btn_cancel;
	private String hint;
	private int inputType;
	private Activity mActivity;
	private String mMessage;
	private String mTitle;
	private OnOKInputClickListener mOnOKClickListener;
	private OnCancelInputClickListener mOnCancelClickListener;

	public DialogPromptInput(Activity activity, String hint, int inputType, String message, String title) {
		super(activity);
		this.hint = hint;
		this.inputType = inputType;
		mActivity = activity;
		mMessage = message;
		mTitle = title;
		setCanceledOnTouchOutside(true);
	}

	public DialogPromptInput(Activity activity, String hint, int inputType, String message, String title, boolean isTouchOutside) {
		super(activity);
		mActivity = activity;
		this.hint = hint;
		this.inputType = inputType;
		mMessage = message;
		mTitle = title;
		setCanceledOnTouchOutside(isTouchOutside);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_promptinput);

		et_dialog_message = (EditText) findViewById(R.id.et_dialog_message);
		btn_cancel = (Button) findViewById(R.id.btn_dialog_syman_cancel);
		btn_ok = (Button) findViewById(R.id.btn_dialog_syman_ok);
		tv_dialog_title = (TextView) findViewById(R.id.tv_dialog_title);

		initData();
		initView();
	}

	public void initData() {
		et_dialog_message.setHint(hint);
		if (inputType == InputType.TYPE_NUMBER_FLAG_DECIMAL) {
			et_dialog_message.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		} else if (inputType == InputType.TYPE_CLASS_NUMBER) {
			et_dialog_message.setInputType(InputType.TYPE_CLASS_NUMBER);
		}

	}

	public void initView() {
		btn_ok.setVisibility(View.GONE);
		btn_cancel.setVisibility(View.GONE);
		if (!StringUtils.isNull(mMessage)) {
			et_dialog_message.setText(mMessage);
			et_dialog_message.setSelection(mMessage.length());
		}
		if (!StringUtils.isNull(mTitle)) {
			tv_dialog_title.setText(mTitle);
		}
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
	public DialogPromptInput setOkButton(String okContent, OnOKInputClickListener onOKClickListener) {

		if (mActivity != null && mActivity.isFinishing()) {
			return this;
		}

		if (!this.isShowing()) {
			this.show();
		}

		if (okContent != null && !okContent.equals("")) {
			btn_ok.setText(okContent);
		}

		btn_ok.setVisibility(View.VISIBLE);
		mOnOKClickListener = onOKClickListener;
		btn_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mMessage = et_dialog_message.getText().toString();
				dismiss();
				if (mOnOKClickListener != null)
				mOnOKClickListener.oKClicked(mMessage);
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
	public DialogPromptInput setCancelButton(String cancelContent, OnCancelInputClickListener onCancelClickListener) {

		if (mActivity != null && mActivity.isFinishing()) {
			return this;
		}

		if (!this.isShowing()) {
			this.show();
		}

		if (cancelContent != null && !cancelContent.equals("")) {
			btn_cancel.setText(cancelContent);
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

	@Override
	public void dismiss() {
		if (isShowing()) {
			// 隐藏键盘
			InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(et_dialog_message.getWindowToken(), 0);
			super.dismiss();
		}
	}

	public interface OnOKInputClickListener {
		abstract void oKClicked(String text);
	}

	public interface OnCancelInputClickListener {
		abstract void cancelClicked();
	}

}
