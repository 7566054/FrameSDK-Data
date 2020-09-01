package com.supoin.framesdk.base;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.supoin.framesdk.R;


public class DialogProgressData extends Dialog {

    TextView tv_msg;
    ProgressBar prg_total;

    private Activity activity;

    public DialogProgressData(Activity activity) {
        super(activity, R.style.DialogLoading);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_progress_data);

        initView();
    }

    private void initView(){

        tv_msg = (TextView) this.findViewById(R.id.tv_msg);
        prg_total = (ProgressBar) this.findViewById(R.id.prg_total);

        Window window = this.getWindow();
        window.setGravity(Gravity.CENTER);
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.height = (int) (dm.heightPixels * 0.3);
        lp.width = (int) (dm.widthPixels * 0.6);
        lp.alpha = 1f;
        window.setAttributes(lp);

        setCanceledOnTouchOutside(false);
    }


    public void showProgress(String totalMessage, int max, int pos) {
        if (!activity.isFinishing() && !isShowing()) {
            setCancelable(false);
            show();
        }

        if (tv_msg != null) {
            tv_msg.setText(totalMessage);
        }

        if (prg_total!= null){
            this.prg_total.setMax(max);
            this.prg_total.setProgress(pos);
        }
    }

}
