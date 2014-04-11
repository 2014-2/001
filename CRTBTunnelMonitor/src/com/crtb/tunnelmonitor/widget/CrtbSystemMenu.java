package com.crtb.tunnelmonitor.widget;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.entity.MenuSystemItem;

/**
 * System Menu
 * 
 * @author zhouwei
 *
 */
@SuppressLint("ViewConstructor")
public final class CrtbSystemMenu extends PopupWindow {
	
	private static int MaxRow = 3 ;
	
	private Activity mToken ;
	private LayoutInflater mLayoutInflater;
	private ISystemMenuOnclick mListener ;

	public CrtbSystemMenu(Activity owner,int width,int height,List<MenuSystemItem> menus) {
		super(width, height);

		mLayoutInflater = LayoutInflater.from(owner);
		mToken			= owner ;
		
		LinearLayout root = (LinearLayout) mLayoutInflater.inflate(R.layout.menu_system_container, null);
		LinearLayout row  = null ;
		
		if(menus.size() <= 2){
			MaxRow	= menus.size() ; 
		} else {
			MaxRow	= 3 ;
		}
		
		for(int index = 0 , size = menus.size(); index < size ; index++){
			
			final MenuSystemItem item = menus.get(index) ;
			
			if(index % MaxRow == 0){
				row = new LinearLayout(owner);
				row.setOrientation(LinearLayout.HORIZONTAL);
				root.addView(row);
			}
			
			RelativeLayout menu = (RelativeLayout)mLayoutInflater.inflate(R.layout.menu_system_item, null);
			menu.setTag(item);
			menu.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					dismiss() ;
					
					if(mListener != null){
						mListener.onclick(item);
					}
				}
			}) ;
			
			ImageView icon = (ImageView)menu.findViewById(R.id.system_item_icon);
			TextView label = (TextView)menu.findViewById(R.id.system_item_name);
			icon.setBackgroundResource(item.getIcon());
			label.setText(item.getName());
			
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.weight = 1 ;
			row.addView(menu, lp);
		}
		
		setContentView(root);
	}
	
	public void setMenuOnclick(ISystemMenuOnclick l){
		mListener	= l ;
	}

	public void show() {

		if (isShowing()) {
			dismiss();
		} else {
			showAtLocation(mToken.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
		}
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

	public static interface ISystemMenuOnclick {

		public void onclick(MenuSystemItem menu);
	}
}
