package com.supoin.framesdk.ui.view;

import android.view.View;
import android.widget.AdapterView;

import com.blankj.utilcode.util.ToastUtils;
import com.supoin.framesdk.R;
import com.supoin.framesdk.base.BaseListActivity;
import com.supoin.framesdk.base.BaseListMDActivity;
import com.supoin.framesdk.base.DialogSelect;
import com.supoin.framesdk.bean.T_Scan;
import com.supoin.framesdk.ui.adapter.ScanAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyListMD extends BaseListMDActivity {
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
    protected void initView() {

        setHeaderTitle(R.id.tv_0, "条码", 3);
        setHeaderTitle(R.id.tv_1, "数量", 1);

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

        mListView.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDialogSelect(listScan.get(position - 1));
                return true;
            }
        });

    }

    private void showDialogSelect(final T_Scan selectShelf) {

        DialogSelect dialogSelect = new DialogSelect(this, "货架号：222，确定删除？", null, "删除");
        dialogSelect.setOnDialogSelectClick(new DialogSelect.OnDialogSelectClick() {

            @Override
            public void onClick(int which) {
                try {



                } catch (Exception e) {
                    //ToastUtils.showShort(getString(R.string.delete_shelf_fail_));
                }
            }
        });
        dialogSelect.show();
    }
}
