package com.supoin.framesdk.service;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.blankj.utilcode.util.ToastUtils;
import com.supoin.framesdk.base.BaseActivity;
import com.supoin.framesdk.bean.T_Bluetooth;
import com.supoin.framesdk.configure.FrameGlobalVariable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by zwei on 2018/6/9.
 * 通用蓝牙配置模块，包括：搜索、配对、获取已配对蓝牙
 */

public class BluetoothService {

    private BaseActivity mActivity;
    private Context mContext;
    private IBluetoothListerner resultListerner;
    private List<T_Bluetooth> listData = new ArrayList<>();
    private BluetoothAdapter mBtAdapter;
    private BluetoothDevice mDevice;
    private int opeType = 0; //0：解除 1：配对

    public BluetoothService(Context context)
    {
        this.mContext = context;
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mActivity = (BaseActivity) context;
    }

    public void initBluetooth()
    {
        //获取已绑定的蓝牙打印机
        List<T_Bluetooth> listBluetoothDevice = getPairedDevices();
        if (listBluetoothDevice != null && listBluetoothDevice.size() > 0) {
            FrameGlobalVariable.BluetoothAddress = listBluetoothDevice.get(0).getBluetoothAddress();
        } else {
            FrameGlobalVariable.BluetoothAddress = "";
        }
    }

    public BluetoothService(Context context, IBluetoothListerner resultListerner)
    {
        this.resultListerner = resultListerner;
        this.mContext = context;
        this.mActivity = (BaseActivity) context;
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    }

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

    public List<T_Bluetooth> getPairedDevices() {
        if (isBluetoothOpen()) {
            Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    T_Bluetooth item = new T_Bluetooth();
                    item.setBluetoothName(device.getName());
                    item.setBluetoothAddress(device.getAddress());
                    item.setPairedStatus("已配对");
                    listData.add(item);
                }
            }
        }
        return listData;
    }

    public void searchBluetooth()
    {
        try {
            if (isBluetoothOpen()) {
                if (mBtAdapter.isDiscovering()) {
                    mBtAdapter.cancelDiscovery();
                }

                listData.clear();

                IntentFilter filter = new IntentFilter();
                filter.addAction(BluetoothDevice.ACTION_FOUND);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                mContext.registerReceiver(mSearchReceiver, filter);

                mBtAdapter.startDiscovery();
                mActivity.showDialogLoading("正在搜索蓝牙打印机...", false);
            }
        }
        catch (Exception ex) {
            ToastUtils.showShort("搜索蓝牙打印机出现失败！原因：" + ex.getMessage());
        }
    }

    public void unpairDevice(String bluetoothAddress) {
        try {
            if (isBluetoothOpen()) {
                mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(
                        bluetoothAddress);
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    mActivity.showDialogLoading("正在解除蓝牙打印机...", false);
                    opeType = 0;
                    IntentFilter boundFilter = new IntentFilter(
                            BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                    mContext.registerReceiver(mPairReceiver, boundFilter);
                    Method createBondMethod = BluetoothDevice.class
                            .getMethod("removeBond");
                    createBondMethod.invoke(mDevice);
                } else {
                    ToastUtils.showShort("该打印机已配对");
                }
            }
        }
        catch (Exception ex) {
            ToastUtils.showShort("解除配对失败！原因：" + ex.getMessage());
        }
    }

    public void pairBluetooth(String bluetoothAddress)
    {
        try {
            if (isBluetoothOpen()) {
                mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(
                        bluetoothAddress);
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    mActivity.showDialogLoading("正在配对蓝牙打印机...", false);
//                    progressDialog = AlertUtil.showNoButtonProgressDialog(mContext, "");
                    opeType = 1;
                    IntentFilter boundFilter = new IntentFilter(
                            BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                    mContext.registerReceiver(mPairReceiver, boundFilter);
                    Method createBondMethod = BluetoothDevice.class
                            .getMethod("createBond");
                    createBondMethod.invoke(mDevice);
                } else {
                    ToastUtils.showShort("该打印机已配对");
                }
            }
        }
        catch (Exception ex) {
            ToastUtils.showShort("配对蓝牙打印机出现失败！原因：" + ex.getMessage());
        }
    }

    private final BroadcastReceiver mSearchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device != null && device.getName() !=null) {
                    T_Bluetooth item = new T_Bluetooth();
                    item.setBluetoothAddress(device.getAddress());
                    item.setBluetoothName(device.getName());
                    String pairedStatus = device.getBondState() == BluetoothDevice.BOND_BONDED ? "已配对" : "";
                    item.setPairedStatus(pairedStatus);
                    for (T_Bluetooth bluetoothDevice : listData)
                    {
                        if (bluetoothDevice.getBluetoothName().equals(item.getBluetoothName()))
                        {
                            return;
                        }
                    }
                    listData.add(item);
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {

                mActivity.dismissLoadingDialog();
                resultListerner.SearchDevices(listData);
            }
        }
    };

    private BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!mDevice.equals(device)) {
                    return;
                }
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDED:
                        //保存已配对的地址
                        FrameGlobalVariable.BluetoothAddress = device.getAddress();

                        mActivity.dismissLoadingDialog();

                        mContext.unregisterReceiver(mPairReceiver);
                        resultListerner.IsPair(true);

                        break;
                    case BluetoothDevice.BOND_NONE:

                        mActivity.dismissLoadingDialog();

                        mContext.unregisterReceiver(mPairReceiver);
                        //解除蓝牙配对成功调用IsUnPair传true,配对失败调用IsPair传false
                        if (opeType == 1)
                            resultListerner.IsPair(false);
                        else
                            resultListerner.IsUnPair(true);

                        break;
                    default:
                        break;
                }
            }
        }
    };

    public interface IBluetoothListerner {
        void SearchDevices(List<T_Bluetooth> mDevices);
        void IsPair(boolean isPair);
        void IsUnPair(boolean isUnPair);
    }
}
