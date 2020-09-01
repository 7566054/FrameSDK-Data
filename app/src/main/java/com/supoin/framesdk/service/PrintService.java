package com.supoin.framesdk.service;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.blankj.utilcode.util.ToastUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.supoin.framesdk.configure.FrameGlobalVariable;

import java.io.OutputStream;
import java.util.Hashtable;
import java.util.UUID;

/**
 * Created by zwei on 2019/9/9.
 * 通用蓝牙打印模块
 */

public class PrintService {

    /**
     * 复位打印机
     */
    public final byte[] RESET = {0x1b, 0x40};

    /**
     * 左对齐
     */
    public final byte[] ALIGN_LEFT = {0x1b, 0x61, 0x00};

    /**
     * 中间对齐
     */
    public final byte[] ALIGN_CENTER = {0x1b, 0x61, 0x01};

    /**
     * 右对齐
     */
    public final byte[] ALIGN_RIGHT = {0x1b, 0x61, 0x02};

    /**
     * 选择加粗模式
     */
    public final byte[] BOLD = {0x1b, 0x45, 0x01};

    /**
     * 取消加粗模式
     */
    public final byte[] BOLD_CANCEL = {0x1b, 0x45, 0x00};

    /**
     * 宽高加倍
     */
    public final byte[] DOUBLE_HEIGHT_WIDTH = {0x1d, 0x21, 0x11};

    /**
     * 宽加倍
     */
    public final byte[] DOUBLE_WIDTH = {0x1d, 0x21, 0x10};

    /**
     * 高加倍
     */
    public final byte[] DOUBLE_HEIGHT = {0x1d, 0x21, 0x01};

    /**
     * 字体不放大
     */
    public final byte[] NORMAL = {0x1d, 0x21, 0x00};

    /**
     * 设置默认行间距
     */
    public final byte[] LINE_SPACING_DEFAULT = {0x1b, 0x32};

    /**
     * 加载数据
     */
    private BluetoothAdapter mBtAdapter;
    private static BluetoothSocket bluetoothSocket;
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Context mContext;

    /**
     * @param context 初始化
     */
    public PrintService(Context context)
    {
        this.mContext = context;
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    /**
     * @return 判断蓝牙是否打开
     */
    private boolean isBluetoothOpen()
    {
        if (mBtAdapter != null && !mBtAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
            mContext.startActivity(enableIntent);
            ToastUtils.showShort("设备蓝牙功能没有打开，请重试！");
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * @return 连接蓝牙
     */
    public boolean connectBluetooth()
    {
        try {
            if (isBluetoothOpen()) {

                //已连接成功后，则不再进行连接
                if (FrameGlobalVariable.BluetoothAddress.equals(""))
                {
                    ToastUtils.showShort("请先配对蓝牙打印机");
                    return false;
                }
                BluetoothDevice mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(
                        FrameGlobalVariable.BluetoothAddress);
                bluetoothSocket = mDevice.createRfcommSocketToServiceRecord(uuid);
                bluetoothSocket.connect();
                return true;
            }
            else{
                ToastUtils.showShort("设备蓝牙模块没有打开");
                return false;
            }
        }
        catch (Exception ex) {
            ToastUtils.showShort("连接蓝牙打印机出现失败！原因：" + ex.getMessage());
            return false;
        }
    }

    /**
     * 断开蓝牙打印
     */
    public void disConnectBluetooth()
    {
        try {
            if (bluetoothSocket != null){
                bluetoothSocket.close();
                bluetoothSocket = null;
            }
        }
        catch (Exception ex) {
            ToastUtils.showShort("释放蓝牙打印机出现异常！原因：" + ex.getMessage());
        }
    }

    /**
     * @param text 打印字符串
     */
    public void printText(String text) {
        try {
            byte[] data = text.getBytes("gbk");
            OutputStream outputStream= bluetoothSocket.getOutputStream();
            outputStream.write(data, 0, data.length);
            outputStream.flush();
        } catch (Exception ex) {
            ToastUtils.showShort("打印失败！原因：" + ex.getMessage());
        }
    }

    public void printBarcode(String text){

        try {
            OutputStream outputStream = bluetoothSocket.getOutputStream();

            outputStream.write(0x1B);
            outputStream.write(97);
            outputStream.write(1);//字体居中
            outputStream.flush();

            //条码的宽度
            outputStream.write(0x1D);
            outputStream.write("w".getBytes());
            outputStream.write(2);//默认是2  2-6 之间
            outputStream.flush();

            //条码的高度
            outputStream.write(0x1D);
            outputStream.write("h".getBytes());
            outputStream.write(80);//默认是60
            outputStream.flush();

            //条码注释打印在条码下方
            outputStream.write(0x1D);
            outputStream.write(72);
            outputStream.write(2);
            outputStream.flush();

            //打印条码样式
            outputStream.write(0x1D);
            outputStream.write("k".getBytes());
            //选择code128
            outputStream.write(73);
            //设置字符个数
            outputStream.write(getStringLen(text));
            //使用CODEB来打印
            outputStream.write(123);
            outputStream.write(66);
            //条形码内容
            outputStream.write(text.getBytes());
            outputStream.flush();
        }
        catch (Exception ex) {
            ToastUtils.showShort("打印失败！原因：" + ex.getMessage());
        }
    }

    //打印二维条码
    public void printQr(String qrData, int width, int height){
        try {
            //判断URL合法性
            if (qrData == null || "".equals(qrData) || qrData.length() < 1)
            {
                ToastUtils.showShort("打印的条码不能为空");
                return;
            }
            Bitmap bitmap = createQRcode(qrData, width, height);
            if (bitmap != null){
                byte[] printByte = draw2PxPoint(bitmap);
                selectCommand(ALIGN_CENTER);
                selectCommand(printByte);
            }

        }
        catch (Exception ex) {
            ToastUtils.showShort("打印失败！原因：" + ex.getMessage());
        }
    }

    /**
     * 生成二维码Bitmap图片
     *
     */
    private Bitmap createQRcode(String url, int w, int h) throws WriterException {

        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //图像数据转换，使用了矩阵转换
        BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, w, h, hints);
        int[] pixels = new int[w * h];
        //下面这里按照二维码的算法，逐个生成二维码的图片，
        //两个for循环是图片横列扫描的结果
        for (int y = 0; y < h; y++)
        {
            for (int x = 0; x < w; x++)
            {
                if (bitMatrix.get(x, y))
                {
                    pixels[y * w + x] = 0xff000000;
                }
                else
                {
                    pixels[y * w + x] = 0xffffffff;
                }
            }
        }
        //生成二维码图片的格式，使用ARGB_8888
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }

    private byte[] draw2PxPoint(Bitmap bmp) {
        //用来存储转换后的 bitmap 数据。为什么要再加1000，这是为了应对当图片高度无法
        //整除24时的情况。比如bitmap 分辨率为 240 * 250，占用 7500 byte，
        //但是实际上要存储11行数据，每一行需要 24 * 240 / 8 =720byte 的空间。再加上一些指令存储的开销，
        //所以多申请 1000byte 的空间是稳妥的，不然运行时会抛出数组访问越界的异常。
        int size = bmp.getWidth() * bmp.getHeight() / 8 + 1000;
        byte[] data = new byte[size];
        int k = 0;
        //设置行距为0的指令
        data[k++] = 0x1B;
        data[k++] = 0x33;
        data[k++] = 0x00;
        // 逐行打印
        for (int j = 0; j < bmp.getHeight() / 24f; j++) {
            //打印图片的指令
            data[k++] = 0x1B;
            data[k++] = 0x2A;
            data[k++] = 33;
            data[k++] = (byte) (bmp.getWidth() % 256); //nL
            data[k++] = (byte) (bmp.getWidth() / 256); //nH
            //对于每一行，逐列打印
            for (int i = 0; i < bmp.getWidth(); i++) {
                //每一列24个像素点，分为3个字节存储
                for (int m = 0; m < 3; m++) {
                    //每个字节表示8个像素点，0表示白色，1表示黑色
                    for (int n = 0; n < 8; n++) {
                        byte b = px2Byte(i, j * 24 + m * 8 + n, bmp);
                        data[k] += data[k] + b;
                    }
                    k++;
                }
            }
            data[k++] = 10;//换行
        }
        return data;
    }

    /**
     * 灰度图片黑白化，黑色是1，白色是0
     *
     * @param x   横坐标
     * @param y   纵坐标
     * @param bit 位图
     * @return
     */
    private byte px2Byte(int x, int y, Bitmap bit) {
        if (x < bit.getWidth() && y < bit.getHeight()) {
            byte b;
            int pixel = bit.getPixel(x, y);
            int red = (pixel & 0x00ff0000) >> 16; // 取高两位
            int green = (pixel & 0x0000ff00) >> 8; // 取中两位
            int blue = pixel & 0x000000ff; // 取低两位
            int gray = RGB2Gray(red, green, blue);
            if (gray < 128) {
                b = 1;
            } else {
                b = 0;
            }
            return b;
        }
        return 0;
    }

    /**
     * 图片灰度的转化
     */
    private int RGB2Gray(int r, int g, int b) {
        int gray = (int) (0.29900 * r + 0.58700 * g + 0.11400 * b);  //灰度转化公式
        return gray;
    }

    private int getStringLen(String str)
    {
        int len = 0;
        for (int i = 0; i < str.length(); i++){
            if (Character.isDigit(str.charAt(i))){
                len +=1;
            }
            else{
                len +=2;
            }
        }
        return len;
    }

    /**
     * @param command 打印命令
     */
    public void selectCommand(byte[] command) {
        try {
            OutputStream outputStream= bluetoothSocket.getOutputStream();
            outputStream.write(command);
            outputStream.flush();
        } catch (Exception ex) {
            ToastUtils.showShort("打印失败！原因：" + ex.getMessage());
        }
    }

    /**
     * 补齐不足长度
     * @param length 长度
     * @param str 数字
     * @return
     */
    public String rpad(int length, String str)
    {
        if (length < 0 || length==0)
            return "";
        String f = "%-" + length + "s";
        return String.format(f, str);
    }

    /**
     * 获取字符串的长度，如果有中文，则每个中文字符计为2位
     * @param value 指定的字符串
     * @return 字符串的长度
     */
    public static int length(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        for (int i = 0; i < value.length(); i++) {
            /* 获取一个字符 */
            String temp = value.substring(i, i + 1);
            /* 判断是否为中文字符 */
            if (temp.matches(chinese)) {
                /* 中文字符长度为2 */
                valueLength += 2;
            } else {
                /* 其他字符长度为1 */
                valueLength += 1;
            }
        }
        return valueLength;
    }
}
