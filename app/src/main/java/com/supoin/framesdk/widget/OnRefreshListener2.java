package com.supoin.framesdk.widget;

import android.view.View;

public interface OnRefreshListener2 <V extends View> {
    // TODO These methods need renaming to START/END rather than DOWN/UP

    /**
     * onPullDownToRefresh will be called only when the user has Pulled from
     * the start, and released.
     */
    public void onPullDownToRefresh(final PullToRefreshBase<V> refreshView);

    /**
     * onPullUpToRefresh will be called only when the user has Pulled from
     * the end, and released.
     */
    public void onPullUpToRefresh(final PullToRefreshBase<V> refreshView);
}
