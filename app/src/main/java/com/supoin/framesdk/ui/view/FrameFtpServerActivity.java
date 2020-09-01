package com.supoin.framesdk.ui.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.supoin.framesdk.R2;
import com.supoin.framesdk.base.BaseActivity;
import com.supoin.framesdk.R;
import com.supoin.framesdk.service.FtpService;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author 安仔夏天勤奋
 * Create Date is  2019/9/6
 * Des Ftp
 */
public class FrameFtpServerActivity extends BaseActivity {

    @BindView(R2.id.start_stop_button_text)
    TextView startStopButtonText;
    @BindView(R2.id.ip_address)
    TextView mIpText;
    @BindView(R2.id.instruction)
    TextView mInstructionText;
    @BindView(R2.id.instruction_pre)
    TextView mInstructionTextPre;
    @BindView(R2.id.start_stop_button)
    LinearLayout mStartStopButton;
    @BindView(R2.id.wifi_state_image)
    ImageView wifiImg;
    @BindView(R2.id.wifi_state)
    TextView wifiState;

    private Context mContext;
    boolean flag =true;//判断当前按钮的状态，TRUE为按钮字为“启动服务”
    private Intent serviceIntent;

    /**
     * @return 布局资源文件id
     */
    @Override
    public int getLayoutId() {
        return R.layout.activity_frame_ftp_server;
    }

    /**
     * 设置标题
     */
    @Override
    protected void initView() {
        setTileText("FTP导出");
        mContext = this;
    }

    /**
     * 数据初始化
     */
    @Override
    protected void loadData() {
        serviceIntent = new Intent(FrameFtpServerActivity.this, FtpService.class);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("MainActivity.wifiOnlineReceiver");
        intentFilter.addAction("MainActivity.wifiOutlineReceiver");
        this.registerReceiver(wifiOnlineReceiver,intentFilter);
    }

    /**
     * 启动\暂停 FTP
     */
    @OnClick(R2.id.start_stop_button)
    void OnStartStop(){
        try {
            serviceIntent = new Intent(FrameFtpServerActivity.this, FtpService.class);
            if (flag) {
                if (Environment.MEDIA_MOUNTED.equals(Environment
                        .getExternalStorageState())) {
                    mContext.startService(serviceIntent);
                    updateUi(true);
                } else {
                    Toast.makeText(FrameFtpServerActivity.this, R.string.ftp_storage_warning, Toast.LENGTH_SHORT).show();
                }
                flag =false;
            } else {
                if(FtpService.server != null){
                    mContext.stopService(serviceIntent);
                    FtpService.server.stop();
                    updateUi(false);
                }
                flag =true;
            }
        }catch (Exception ex){
            ToastUtils.showShort("FTP启动出现错误，原因：" + ex.getMessage());
        }
    }

    /**
     * 可以弹出系统wifi界面进行设置
     */
    @OnClick(R2.id.wifi_state_image)
    void OnWifiState(){
        Intent intentWifi = new Intent(
                android.provider.Settings.ACTION_WIFI_SETTINGS);
        startActivity(intentWifi);
    }

    public String getLocalIpAddress() throws SocketException {
        for (Enumeration<NetworkInterface> en = NetworkInterface
                .getNetworkInterfaces(); en.hasMoreElements();) {
            NetworkInterface intf = en.nextElement();
            for (Enumeration<InetAddress> enumIpAddr = intf
                    .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                InetAddress inetAddress = enumIpAddr.nextElement();
                if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                    return inetAddress.getHostAddress();
                }
            }
        }
        return null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * FTP 界面刷新
     * @param running
     */
    private void updateUi(boolean running) {

        try {
            WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            // int wifiState = wifiMgr.getWifiState();
            WifiInfo info = wifiMgr.getConnectionInfo();
            String wifiId = info != null ? info.getSSID() : null;
            boolean isWifiReady = NetworkUtils.isWifiConnected();// FtpService.isConnectedUsingWifi();

            wifiState.setText(isWifiReady ? wifiId : getString(R.string.ftp_no_wifi_hint));
            wifiImg.setImageResource(isWifiReady ? R.mipmap.ftp_wifi_state4
                    : R.mipmap.ftp_wifi_state0);

            if (running) {
                // Put correct text in start/stop button
                // Fill in wifi status and address
                String address = getLocalIpAddress();
                if (address != null) {
                    mIpText.setText("ftp://" + getLocalIpAddress() + ":2221");
                } else {
                    Context context = getApplicationContext();
                    context.stopService(serviceIntent);
                    mIpText.setText("");
                    mIpText.setVisibility(View.GONE);
                }
            }

            mStartStopButton.setEnabled(isWifiReady);
            if (isWifiReady) {
                startStopButtonText.setText(running ? R.string.ftp_stop_server
                        : R.string.ftp_start_server);
                startStopButtonText.setCompoundDrawablesWithIntrinsicBounds(
                        running ? R.mipmap.ftp_disconnect : R.mipmap.ftp_connect, 0, 0,
                        0);
                startStopButtonText.setTextColor(running ? getResources().getColor(
                        R.color.grey) : getResources().getColor(
                        R.color.red));
            } else {
                if (running) {
                    Context context = getApplicationContext();
                    context.stopService(serviceIntent);
                }

                startStopButtonText.setText(R.string.ftp_no_wifi);
                startStopButtonText.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                        0, 0);
                startStopButtonText.setTextColor(Color.GRAY);
                mIpText.setVisibility(View.GONE);
                mInstructionText.setVisibility(View.GONE);
                mInstructionTextPre.setVisibility(View.GONE);
            }

            mIpText.setVisibility(running ? View.VISIBLE : View.GONE);
            mInstructionText.setVisibility(running ? View.VISIBLE : View.GONE);
            mInstructionTextPre.setVisibility(running ? View.GONE : View.VISIBLE);
        }
        catch (Exception ex)
        {
            ToastUtils.showShort("界面更新出错，原因：" + ex.getMessage());
        }
    }

    /**
     * 销毁事件
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(wifiOnlineReceiver);
    }

    /**
     * 页面进入事件
     */
    @Override
    protected void onResume() {
        super.onResume();
        if(FtpService.server!=null){
            if(FtpService.server.isStopped()){
                updateUi(false);
                flag =true;
            }else{
                updateUi(true);
                flag =false;
            }
        }else{
            updateUi(false);
            flag =true;
        }

    }

    /**
     * 暂停事件
     */
    @Override
    protected void onStop() {
        super.onStop();
        if(FtpService.server!=null){
            if(FtpService.server.isStopped()){
                updateUi(false);
                flag =true;
            }else{
                updateUi(true);
                flag =false;
            }
        }else{
            updateUi(false);
            flag =true;
        }
    }

    /** 联网后检查更新 */
    BroadcastReceiver wifiOnlineReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("MainActivity.wifiOnlineReceiver".equals(intent.getAction())) {
                if(FtpService.server!=null){
                    if(FtpService.server.isStopped()){
                        updateUi(false);
                        flag =true;
                    }else{
                        updateUi(true);
                        flag =false;
                    }
                }else{
                    updateUi(false);
                    flag =true;
                }
            } else if ("MainActivity.wifiOutlineReceiver".equals(intent.getAction())) {
                if(FtpService.server != null){
                    mContext.stopService(serviceIntent);
                    FtpService.server.stop();
                    updateUi(false);
                }
                flag =true;
            }
        }
    };
}
