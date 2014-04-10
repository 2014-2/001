package com.crtb.tunnelmonitor.widget;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.crtb.tunnelmonitor.activity.R;

@SuppressLint("ViewConstructor")
public final class CrtbItemPopMenu<T> extends PopupWindow {

	private List<String> menuList = new ArrayList<String>();
	private LayoutInflater mLayoutInflater;
	private T mTag;
	private IMenuOnclick<T> mListener ;

	public CrtbItemPopMenu(Context context, int width, int height,
			String[] menus) {
		super(width, height);

		if (menus == null) {
			throw new RuntimeException("Error : CrtbItemPopMenu item is null");
		}

		mLayoutInflater = LayoutInflater.from(context);

		for (String item : menus) {
			menuList.add(item);
		}

		ListView list = new ListView(context);
		list.setAdapter(new MenuAdapter());
		list.setDivider(null);
		setContentView(list);
	}
	
	public void setMenuOnclick(IMenuOnclick<T> l){
		mListener	= l ;
	}

	public void show(View anchor, int xoff, T bean) {

		mTag = bean;

		if (isShowing()) {
			dismiss();
		}

		showAsDropDown(anchor, xoff, 0);
	}
	
	public void onTouchEvent(MotionEvent event){
		
		Rect outRect = new Rect() ;
		getContentView().getHitRect(outRect);
		
		int dx = (int)event.getX() ;
		int dy = (int)event.getY() ;
		
		if(!outRect.contains(dx, dy)){
			dismiss() ;
		}
	}

	private class MenuAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return menuList.size();
		}

		@Override
		public String getItem(int position) {
			return menuList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.menu_item, null);
			}
			
			final String menu = getItem(position) ;

			TextView tv = (TextView) convertView.findViewById(R.id.menu_name);
			tv.setText(menu);

			View layout = convertView.findViewById(R.id.menu_item_layout);
			layout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					dismiss();

					if(mListener != null){
						mListener.onclick(menu, mTag);
					}
				}
			});

			return convertView;
		}
	}

	public static interface IMenuOnclick<T> {

		public void onclick(String menu, T bean);
	}
}
