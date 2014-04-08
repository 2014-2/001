package com.crtb.tunnelmonitor.utils;


import com.crtb.tunnelmonitor.activity.RecordNewActivity;
import com.crtb.tunnelmonitor.activity.SectionEditActivity;
import com.crtb.tunnelmonitor.activity.SectionNewActivity;
import com.crtb.tunnelmonitor.activity.WorkActivity;
import com.crtb.tunnelmonitor.activity.WorkNewActivity;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.activity.R.id;
import com.crtb.tunnelmonitor.activity.R.layout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
/**
 * 菜单
 */
public class SelectPicPopupWindow extends PopupWindow {
	private RelativeLayout xinjian;
	public RelativeLayout daoru;
	private View mMenuView;
	private Intent intent;
	public Context c;
	public SelectPicPopupWindow(Activity context, OnClickListener itemsOnClick, final int num,final int currIndex) {
		super(context);
		this.c = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.alert_dialog, null);
		xinjian = (RelativeLayout) mMenuView.findViewById(R.id.r1);
		daoru = (RelativeLayout) mMenuView.findViewById(R.id.r6);
		if (num == 2 || num == 3) {
			daoru.setVisibility(View.GONE);
		}
		xinjian.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (num == 1) {
					intent = new Intent(c,WorkNewActivity.class);
					intent.putExtra(Constant.Select_WorkRowClickItemsName_Name,"");
					((Activity)c).startActivityForResult(intent,0);
					//c.startActivity(intent);
				}else if (num == 2) {
					if (currIndex == 1) {
						intent = new Intent(c,
								SectionEditActivity.class);
						intent.putExtra(Constant.Select_SectionRowClickItemsName_Name,"");
						((Activity)c).startActivityForResult(intent,0);
						//c.startActivity(intent);
					} else {
						intent = new Intent(c,
								SectionNewActivity.class);
						intent.putExtra(Constant.Select_SectionRowClickItemsName_Name,"");
						((Activity)c).startActivityForResult(intent,0);
						//c.startActivity(intent);
					}
					
				}else if (num == 3) {
					if (currIndex == 0) {
						Intent intent = new Intent(c,
								RecordNewActivity.class);
						Bundle mBundle = new Bundle();  
						mBundle.putInt(Constant.Select_RecordRowClickItemsName_Name, 1);
				        mBundle.putParcelable(Constant.Select_RecordRowClickItemsName_Data, null);
				        intent.putExtras(mBundle);
						((Activity)c).startActivityForResult(intent,0);
						//c.startActivity(i);
					} else {
						Intent intent = new Intent(c,
								RecordNewActivity.class);
						Bundle mBundle = new Bundle();  
						mBundle.putInt(Constant.Select_RecordRowClickItemsName_Name, 3);
				        mBundle.putParcelable(Constant.Select_RecordRowClickItemsName_Data, null);
				        intent.putExtras(mBundle);
						((Activity)c).startActivityForResult(intent,0);
						//System.out.println("sssss");
						//c.startActivity(i);
					}
				}
				
			}
		});
		daoru.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		this.setContentView(mMenuView);
		this.setWidth(LayoutParams.FILL_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xFF000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mMenuView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});

	}

}
