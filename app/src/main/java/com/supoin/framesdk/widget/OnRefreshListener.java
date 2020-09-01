package com.supoin.framesdk.widget;

import android.view.View;
import android.widget.AdapterView;

public interface OnRefreshListener <V extends View> {
    public void onRefresh(final PullToRefreshBase<V> refreshView);

    void onItemClick(AdapterView<?> arg0, View view, int position, long id);
}
