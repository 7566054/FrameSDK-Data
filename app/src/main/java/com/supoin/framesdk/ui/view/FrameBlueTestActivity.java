package com.supoin.framesdk.ui.view;

import android.widget.EditText;

import com.blankj.utilcode.util.ToastUtils;
import com.supoin.framesdk.R;
import com.supoin.framesdk.R2;
import com.supoin.framesdk.base.BaseActivity;
import com.supoin.framesdk.service.BluetoothService;
import com.supoin.framesdk.service.PrintService;
import com.supoin.framesdk.utils.CreateThreadPool;
import com.supoin.framesdk.utils.StringUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author: zzw
 * Date: 2020-06-10
 * Describe:
 */
public class FrameBlueTestActivity extends BaseActivity {

    @BindView(R2.id.et_text)
    EditText et_text;

    @BindView(R2.id.et_barcode)
    EditText et_barcode;

    @BindView(R2.id.et_qrCode)
    EditText et_qrCode;

    PrintService printService;

    @Override
    public int getLayoutId() {
        return R.layout.activity_frame_blue_test;
    }

    @Override
    protected void initView() {
        setTileText("蓝牙打印测试");
    }

    @Override
    protected void loadData() {

        BluetoothService bluetoothService = new BluetoothService(this);
        bluetoothService.initBluetooth();

        printService = new PrintService(this);

        et_text.setText("                销邦蓝牙打印测试 \n\n" +
                "订单编号:                201507161515\n" +
                "点菜时间:                2016-02-16 10:46\n" +
                "上菜时间:                2016-02-16 11:46\n" +
                "人数：2人                收银员：张三\n");

        et_barcode.setText("LM20031010152849974");

        et_qrCode.setText("https://blog.csdn.net/mountain_hua");
    }

    @OnClick(R2.id.btn_print_text)
    void OnPrintText(){

        String printText = et_text.getText().toString();

        if (printText.length() == 0){
            ToastUtils.showShort("打印机内容不能为空");
            return;
        }

        showDialogLoading("正在打印文本...", true);

        createThreadPool.startThreadPool(new CreateThreadPool.OnInitThreadPool() {
            @Override
            public String onThreadDo() {

                try {
                    if (printService.connectBluetooth()){
                        printService.selectCommand(printService.RESET);

                        printService.printText(printText);
                        printService.printText("\n\n\n");

                        Thread.sleep(500);
                        printService.disConnectBluetooth();

                        return "";
                    }
                    else{
                        return "蓝牙连接失败！请重试";
                    }
                }
                catch (Exception ex){
                    return "打印机出异常，原因："+ ex.getMessage();
                }

            }

            @Override
            public void onThreadFinsih(String error) {
                dismissLoadingDialog();

                if (!StringUtils.isNull(error)) {
                    showDialogText(error, null);
                    return;
                }
                ToastUtils.showShort("打印文本成功！");
            }
        });
    }

    @OnClick(R2.id.btn_print_barcode)
    void OnPrintBarcode(){


        String printText = et_barcode.getText().toString().trim();

        if (printText.equals("")){
            ToastUtils.showShort("打印机内容不能为空");
            return;
        }

        showDialogLoading("正在打印条码...", true);
        createThreadPool.startThreadPool(new CreateThreadPool.OnInitThreadPool() {
            @Override
            public String onThreadDo() {

                try {
                    if (printService.connectBluetooth()){
                        printService.selectCommand(printService.RESET);

                        printService.printBarcode(printText);
                        printService.printText("\n\n");

                        Thread.sleep(500);
                        printService.disConnectBluetooth();

                        return "";
                    }
                    else{
                        return "蓝牙连接失败！请重试";
                    }
                }
                catch (Exception ex){
                    return "打印机出异常，原因："+ ex.getMessage();
                }

            }

            @Override
            public void onThreadFinsih(String error) {
                dismissLoadingDialog();

                if (!StringUtils.isNull(error)) {
                    showDialogText(error, null);
                    return;
                }
                ToastUtils.showShort("打印一维条码成功！");
            }
        });
    }

    @OnClick(R2.id.btn_print_qrCode)
    void OnPrintQrCode(){
        String printText = et_qrCode.getText().toString().trim();

        if (printText.equals("")){
            ToastUtils.showShort("打印机内容不能为空");
            return;
        }

        showDialogLoading("正在打印二维条码...", true);
        createThreadPool.startThreadPool(new CreateThreadPool.OnInitThreadPool() {
            @Override
            public String onThreadDo() {

                try {
                    if (printService.connectBluetooth()){
                        printService.selectCommand(printService.RESET);

                        printService.printQr(printText, 200, 200);
                        printService.printText("\n\n\n\n");

                        Thread.sleep(500);
                        printService.disConnectBluetooth();

                        return "";
                    }
                    else{
                        return "蓝牙连接失败！请重试";
                    }
                }
                catch (Exception ex){
                    return "打印机出异常，原因："+ ex.getMessage();
                }

            }

            @Override
            public void onThreadFinsih(String error) {
                dismissLoadingDialog();

                if (!StringUtils.isNull(error)) {
                    showDialogText(error, null);
                    return;
                }
                ToastUtils.showShort("打印二维条码成功！");
            }
        });
    }

}
