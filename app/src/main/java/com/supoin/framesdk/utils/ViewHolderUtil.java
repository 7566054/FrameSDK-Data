package com.supoin.framesdk.utils;

import android.util.SparseArray;
import android.view.View;

/**
 * Created by zhangfan on 2017/5/15 0015.
 *
 * /*
 * SparseArray<View>在代码理解上等价于HashMap<Interger, View>,
 * SparseArray是Android提供的一个数据结构，旨在提高查询的效率.
 * 所以View childView = viewHolder.get(id);这句代码的时间上的开销是极小的，完全不会影响到执行的效率.
 *
 *
 * 在adapter使用
 * public View getView(int position,View convertView,ViewGroup parent){
 *
 *  if(convertView == null)
 *  {
 *  	convertView = mInflater.inflate(R.layout...,parent,false);
 *  }
 *
 *  ImageView img = ViewHolderUtil.get(convertView,R.id.....);
 * 	 return convertView;
 * }
 *
 * */

public class ViewHolderUtil {
    @SuppressWarnings("unchecked")
    public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }
}
