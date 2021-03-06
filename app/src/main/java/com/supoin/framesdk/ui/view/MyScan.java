package com.supoin.framesdk.ui.view;

import android.widget.EditText;

import com.supoin.framesdk.R;
import com.supoin.framesdk.base.BaseScanActivity;
import com.supoin.framesdk.bean.T_Scan;
import com.supoin.framesdk.ui.adapter.ScanAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyScan extends BaseScanActivity {

    private ScanAdapter mAdapter;
    @Override
    public int getCustomLayoutId() {
        return R.layout.activity_custom_scan;
    }

    @Override
    public String getBillTypeName() {
        return "入库单";
    }

    @Override
    protected void initView() {
        EditText editText = viewCustom.findViewById(R.id.et_barcode);
        editText.setText("郑正威");
    }

    @Override
    protected void loadData() {

        setHeaderTitle(R.id.tv_0, "条码", 100);
        setHeaderTitle(R.id.tv_1, "数量", 100);

        List<T_Scan> listScan = new ArrayList<>();

        T_Scan item = new T_Scan();
        item.setBarcode("690001");
        item.setQty(1);
        listScan.add(item);

        item = new T_Scan();
        item.setBarcode("690002");
        item.setQty(2);
        listScan.add(item);

        mAdapter = new ScanAdapter(this, listScan);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void scan(String barcode) {

    }

    @Override
    protected void onBtn_1() {

    }

    @Override
    protected void onBtn_2() {

    }

    @Override
    protected void onBtn_3() {

    }

    @Override
    protected void close() {

    }

    @Override
    protected void onTitleMore() {

    }
}
