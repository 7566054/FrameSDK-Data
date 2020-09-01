package com.supoin.framesdk.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by zwei on 2018/06/20.
 * 简化扫描类
 */

public class ScanUtil {
    private ScanResultListerner resultListerner;
    private static final String SCN_CUST_ACTION_SCODE = "com.android.server.scannerservice.broadcast";
    private static final String SCN_CUST_EX_SCODE = "scannerdata";
    public ScanUtil(Context context, ScanResultListerner resultListerner) {
        this.resultListerner = resultListerner;
        IntentFilter intentFilter = new IntentFilter(SCN_CUST_ACTION_SCODE);
        context.registerReceiver(scanReceiver, intentFilter);
    }

    public void onDestroy(Context context) {
        if(scanReceiver!=null){
            context.unregisterReceiver(scanReceiver);
        }
    }

    private BroadcastReceiver scanReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (resultListerner != null && intent.getAction() != null && intent.getAction().equals(SCN_CUST_ACTION_SCODE)) {
                // 第一代需要这样改m.obj = message.substring(0, message.length()-1);
                // 因为获取到的条码后会加个\n的缘故第二代的不需要，字符串trim方法即可取消空白字符
                String message = intent.getStringExtra(SCN_CUST_EX_SCODE).trim();
                try {
                    resultListerner.scanResult(message);
                } catch (Exception ex) {
                    resultListerner.scanResult("");
                }
            }
        }
    };

    public interface ScanResultListerner {
        void scanResult(String result);
    }
}
