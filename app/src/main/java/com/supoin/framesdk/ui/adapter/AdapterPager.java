package com.supoin.framesdk.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class AdapterPager extends PagerAdapter {
	private List<View> viewList;

	public AdapterPager(List<View> views) {
		this.viewList = views;
	}

	@Override
	public int getCount() {
		return viewList.size();
	}

	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(viewList.get(position), 0);
		return viewList.get(position);
	}

	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(viewList.get(position));
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

}
