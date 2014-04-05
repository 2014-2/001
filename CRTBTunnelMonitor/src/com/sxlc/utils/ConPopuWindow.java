package com.sxlc.utils;


import java.util.List;

import com.sxlc.activity.CRTBTunnelMonitor;
import com.sxlc.activity.ControlNewActivity;
import com.sxlc.activity.ControlNewActivityTwo;
import com.sxlc.activity.ControlPointsActivity;
import com.sxlc.activity.R;
import com.sxlc.activity.StationActivity;
import com.sxlc.common.Constant;
import com.sxlc.dao.impl.ControlPointsDaoImpl;
import com.sxlc.entity.ControlPointsInfo;
import com.sxlc.entity.TotalStationInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class ConPopuWindow extends PopupWindow {
	private RelativeLayout xinjian,bianji,delete;//三個按鈕
	private View mMenuView;
	public Context c;
	AlertDialog dlg = null;
	public ConPopuWindow(Activity context, OnClickListener itemsOnClick,
			final int num, final int currIndex) {
		super(context);
		this.c = context;
		dlg = new AlertDialog.Builder(c).create();
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.kd_diagol, null);
		xinjian = (RelativeLayout) mMenuView.findViewById(R.id.kd_tj);
		bianji = (RelativeLayout) mMenuView.findViewById(R.id.kd_bj);
		delete = (RelativeLayout) mMenuView.findViewById(R.id.kd_sc);

		xinjian.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(c, ControlNewActivityTwo.class);
				Bundle mBundle = new Bundle();  
		        mBundle.putParcelable(Constant.Select_ControlPointsRowClickItemsName_Data,null);
		        intent.putExtras(mBundle);
				((Activity) c).startActivityForResult(intent,0);
			}
		});
		bianji.setOnClickListener(new OnClickListener() {

			//@SuppressLint("ShowToast")
			@Override
			public void onClick(View v) {
				List<ControlPointsInfo> tmpList = ((ControlPointsActivity)c).list;
				ControlPointsInfo tmp = null;
				if (tmpList == null) {
					Toast.makeText((Activity) c, "请选择需要编辑的控制点", 3000).show();
				    return;
				}
				else {
					for (int i = 0; i < tmpList.size(); i++) {
						if (tmpList.get(i).isbCheck()) {
							tmp = tmpList.get(i);
							break;
						}
					}
				}
				if (tmp == null) {
					Toast.makeText((Activity) c, "请选择需要编辑的控制点", 3000).show();
				    return;
				}
				Intent intent = new Intent(c, ControlNewActivityTwo.class);
				Bundle mBundle = new Bundle();  
		        mBundle.putParcelable(Constant.Select_ControlPointsRowClickItemsName_Data,tmp);
		        intent.putExtras(mBundle);
				((Activity) c).startActivityForResult(intent,0);
			}
		});
		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				List<ControlPointsInfo> tmpList = ((ControlPointsActivity)c).list;
				if(tmpList!=null){
				CRTBTunnelMonitor app=(CRTBTunnelMonitor) c.getApplicationContext();
				ControlPointsDaoImpl impl = new ControlPointsDaoImpl(c, app.GetCurWork().getProjectName());
				ControlPointsInfo info=null;
				boolean bCheck=false;
				for(int i=0;i<tmpList.size();i++){
					if(tmpList.get(i).isbCheck()){
						
						bCheck=true;
						if(!tmpList.get(i).isbUse()){
							info=tmpList.get(i);
						}
						break;
					}
				}
				if(!bCheck){
					 showDialog("请先选择要删除的控制点");
					 return;
				}
				if(info==null){
					showDialog("控制点正在使用中，无法删除");
					return;
				}
				impl.DeleteStationInfo(info.getId());
				((ControlPointsActivity)c).list.remove(info);
				((ControlPointsActivity)c).adapter.notifyDataSetChanged();
				}
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
		setOutsideTouchable(true);
	}
	
	private void showDialog(String text) {
		AlertDialog.Builder builder = new Builder(c);
		builder.setTitle("提示");
		builder.setMessage(text);
		builder.setPositiveButton("确定", null);
		builder.create().show();
	}
}
