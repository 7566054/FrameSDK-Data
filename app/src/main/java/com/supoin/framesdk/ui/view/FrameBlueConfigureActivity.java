package com.supoin.framesdk.ui.view;

import android.widget.Button;
import android.widget.ListView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.supoin.framesdk.R;
import com.supoin.framesdk.R2;
import com.supoin.framesdk.base.BaseActivity;
import com.supoin.framesdk.bean.T_Bluetooth;
import com.supoin.framesdk.service.BluetoothService;
import com.supoin.framesdk.service.PrintService;
import com.supoin.framesdk.ui.adapter.FrameBlueConfigureAdapter;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class FrameBlueConfigureActivity extends BaseActivity implements BluetoothService.IBluetoothListerner {

    BluetoothService bluetoothService;

    T_Bluetooth selectMainEntity;
    List<T_Bluetooth> listData = new ArrayList<>();
    private FrameBlueConfigureAdapter adapterData;

    @BindView(R2.id.searchDevices)
    Button searchDevices;

    @BindView(R2.id.listview_data)
    ListView mListView;

    /**
     * @return 布局资源文件id
     */
    @Override
    public int getLayoutId() {
        return R.layout.activity_frame_blue_configure;
    }

    /**
     * 设置标题
     */
    @Override
    protected void initView() {
        setTileText(getString(R.string.bluetooth_configuration));
        bluetoothService = new BluetoothService(this, this);
    }

    @Override
    protected void loadData() {

        listData = bluetoothService.getPairedDevices();
        adapterData = new FrameBlueConfigureAdapter(this, listData);
        mListView.setAdapter(adapterData);

        mListView.setOnItemClickListener((parent, view, position, id) -> {
            ListView lv = (ListView) parent;
            selectMainEntity = (T_Bluetooth) lv.getItemAtPosition(position);
            adapterData.setItemClickPosition(position);
            adapterData.notifyDataSetInvalidated();
        });
    }

    @OnClick(R2.id.searchDevices)
    public void OnSearchDevices()
    {
        bluetoothService.searchBluetooth();
    }

    @OnClick(R2.id.btn_pair)
    public void OnPair()
    {
        if (selectMainEntity != null) {
            bluetoothService.pairBluetooth(selectMainEntity.getBluetoothAddress());
        }
        else{
            ToastUtils.showShort(getString(R.string.select_connect_blue_printer));
        }
    }

    @OnClick(R2.id.btn_unpair)
    public void OnUnPair()
    {
        if (selectMainEntity != null) {
            bluetoothService.unpairDevice(selectMainEntity.getBluetoothAddress());
        }
        else{
            ToastUtils.showShort(getString(R.string.select_connect_blue_printer));
        }
    }

    @OnClick(R2.id.btn_test)
    void OnTest(){
        ActivityUtils.startActivity(FrameBlueTestActivity.class);
    }


    @Override
    public void SearchDevices(List<T_Bluetooth> mDevices) {
        adapterData.notifyDataSetChanged();
    }

    @Override
    public void IsPair(boolean isPair) {
        if (selectMainEntity != null)
        {
            if (isPair) {
                selectMainEntity.setPairedStatus(getString(R.string.paired));
                adapterData.notifyDataSetChanged();
                ToastUtils.showShort(getString(R.string.pairing_successful));
            }
            else {
                ToastUtils.showShort(getString(R.string.pairing_failed));
            }
        }
    }

    @Override
    public void IsUnPair(boolean isUnPair) {
        if (selectMainEntity != null)
        {
            if (isUnPair) {
                selectMainEntity.setPairedStatus("");
                adapterData.notifyDataSetChanged();
                ToastUtils.showShort(getString(R.string.unbinding_succeeded));
            }
            else {
                ToastUtils.showShort(getString(R.string.pairing_failed));
            }
        }
    }

}
