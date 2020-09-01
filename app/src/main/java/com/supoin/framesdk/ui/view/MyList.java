package com.supoin.framesdk.ui.view;

import androidx.fragment.app.FragmentActivity;

import com.supoin.framesdk.base.BaseListActivity;
import com.supoin.framesdk.bean.T_Scan;
import com.supoin.framesdk.ui.adapter.ScanAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyList extends BaseListActivity {
    private ScanAdapter mAdapter;

    @Override
    protected int getCustomLayoutId() {
        return 0;
    }

    @Override
    protected String getBillTypeName() {
        return "入库单";
    }

    @Override
    protected void scan(String barcode) {

    }

    @Override
    protected void createBill() {

    }

    @Override
    protected void initView() {
        isCreateBill = true;
    }

    @Override
    protected void loadData() {
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
}
