package com.supoin.framesdk.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.supoin.framesdk.R;
import com.supoin.framesdk.bean.T_Bluetooth;
import com.supoin.framesdk.utils.ViewHolderUtil;

import java.util.List;

/**
 * Created by zhangfan on 2018/1/15 0015.
 *
 */

public class FrameBlueConfigureAdapter extends BaseAdapter {

    private List<T_Bluetooth> listInfo;
    private LayoutInflater inflater;
    private int itemClickPosition = -1;

   public FrameBlueConfigureAdapter(Context context, List<T_Bluetooth> listInfo){
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
            convertView = inflater.inflate(R.layout.item_list_blue_configure,parent,false);
        }

        TextView tv_BluetoothName = ViewHolderUtil.get(convertView, R.id.tv_BluetoothName);
        TextView tv_PairedStatus = ViewHolderUtil.get(convertView, R.id.tv_PairedStatus);

        tv_BluetoothName.setText(listInfo.get(position).getBluetoothName());
        tv_PairedStatus.setText(listInfo.get(position).getPairedStatus());

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
