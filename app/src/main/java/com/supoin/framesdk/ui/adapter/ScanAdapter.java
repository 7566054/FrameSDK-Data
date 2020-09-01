package com.supoin.framesdk.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.supoin.framesdk.R;
import com.supoin.framesdk.bean.T_Bluetooth;
import com.supoin.framesdk.bean.T_Scan;
import com.supoin.framesdk.utils.ViewHolderUtil;

import java.util.List;

public class ScanAdapter extends BaseAdapter {
    private List<T_Scan> listInfo;
    private LayoutInflater inflater;
    private int itemClickPosition = -1;

    public ScanAdapter(Context context, List<T_Scan> listInfo){
        super();
        this.listInfo = listInfo;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return listInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_scan,parent,false);
        }

        TextView tv_barcode = ViewHolderUtil.get(convertView, R.id.tv_barcode);
        TextView tv_qty = ViewHolderUtil.get(convertView, R.id.tv_qty);

        tv_barcode.setText(listInfo.get(position).getBarcode());
        tv_qty.setText(String.valueOf(listInfo.get(position).getQty()));

        if (itemClickPosition == position) {
            convertView.setBackgroundResource(R.color.list_selected_background);
        } else {
            convertView.setBackgroundResource(R.color.white);
        }

        return convertView;
    }

    public void setItemClickPosition(int itemClickPosition) {
        this.itemClickPosition = itemClickPosition;
    }
}
