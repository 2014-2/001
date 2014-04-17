package com.crtb.tunnelmonitor.utils;


import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.activity.ControlNewActivityTwo;
import com.crtb.tunnelmonitor.activity.ControlPointsActivity;
import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.v2.ControlPointsInfoDao;
import com.crtb.tunnelmonitor.entity.ControlPointsInfo;

public class ConPopuWindow extends PopupWindow {
    private RelativeLayout shiyong, xinjian, bianji, delete, qita;// 五个按钮

    private View mMenuView;

    public Context c;

    AlertDialog dlg = null;

    private ControlPointsInfo mInfo;

    public ConPopuWindow(Activity context, OnClickListener itemsOnClick,
            final int num, final int currIndex) {
        super(context);
        this.c = context;
        dlg = new AlertDialog.Builder(c).create();
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.kd_diagol, null);
        shiyong = (RelativeLayout)mMenuView.findViewById(R.id.kd_sy);
        xinjian = (RelativeLayout) mMenuView.findViewById(R.id.kd_tj);
        bianji = (RelativeLayout) mMenuView.findViewById(R.id.kd_bj);
        delete = (RelativeLayout) mMenuView.findViewById(R.id.kd_sc);
        qita = (RelativeLayout)mMenuView.findViewById(R.id.kd_qt);

        shiyong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ControlPointsInfo> tmpList = ((ControlPointsActivity)c).mControlPoints;
                ControlPointsInfo tmp = null;
                int iItemPos = 0;
                if (tmpList == null) {
                    Toast.makeText(c, "请选择要使用的控制点", 3000).show();
                    return;
                } else {
                    for (int i = 0; i < tmpList.size(); i++) {
                        if (tmpList.get(i).isChecked()) {
                            tmp = tmpList.get(i);
                            iItemPos = i;
                            break;
                        }
                    }
                }
                if (tmp == null) {
                    Toast.makeText(c, "请选择要使用的控制点", 3000).show();
                    return;
                }
                tmp.setUsed(true);
                tmp.setChecked(true);
                ((ControlPointsActivity)c).mControlPoints.set(iItemPos, tmp);
                for (int i = 0; i < ((ControlPointsActivity)c).mControlPoints.size(); i++) {
                    if (i != iItemPos) {
                        ((ControlPointsActivity)c).mControlPoints.get(i).setUsed(false);
                        ((ControlPointsActivity)c).mControlPoints.get(i).setChecked(false);
                    }
                }
                ControlPointsInfoDao.defaultDao().update(tmp);
                ((ControlPointsActivity)c).mAdapter.notifyDataSetChanged();
            }
        });

        xinjian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c, ControlNewActivityTwo.class);
                Bundle mBundle = new Bundle();
                mBundle.putBoolean("bEdit", false);
                mBundle.putParcelable(Constant.Select_ControlPointsRowClickItemsName_Data,null);
                intent.putExtras(mBundle);
                ((Activity) c).startActivityForResult(intent,0);
            }
        });
        bianji.setOnClickListener(new View.OnClickListener() {

            //@SuppressLint("ShowToast")
            @Override
            public void onClick(View v) {
                List<ControlPointsInfo> tmpList = ((ControlPointsActivity)c).mControlPoints;
                ControlPointsInfo tmp = null;
                if (tmpList == null) {
                    Toast.makeText(c, "请选择需要编辑的控制点", 3000).show();
                    return;
                }
                else {
                    for (int i = 0; i < tmpList.size(); i++) {
                        if (tmpList.get(i).isChecked()) {
                            tmp = tmpList.get(i);
                            break;
                        }
                    }
                }
                if (tmp == null) {
                    Toast.makeText(c, "请选择需要编辑的控制点", 3000).show();
                    return;
                }
                if (tmp.isUsed()) {

                    showDialog("该控制点正在使用中,无法编辑", null);
                    return;
                }
                Intent intent = new Intent(c, ControlNewActivityTwo.class);
                Bundle mBundle = new Bundle();
                mBundle.putBoolean("bEdit", true);
                mBundle.putSerializable(Constant.Select_ControlPointsRowClickItemsName_Data, tmp);
                intent.putExtras(mBundle);
                ((Activity)c).startActivityForResult(intent, 0);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            	// 所以控制点
            	List<ControlPointsInfo> list = ControlPointsInfoDao.defaultDao().queryAllControlPoints();
            	
            	if(list != null){
            		
            		for(final ControlPointsInfo info : list){
            			
            			if(info.isChecked()){
            				
            				if(!info.isUsed()){
            					
            					showDialog("删除后数据无法恢复，确定删除？",new dialogListener(){

									@Override
									public void onClickOk() {
										
										ControlPointsInfoDao.defaultDao().delete(info);
										ControlPointsActivity.mAdapter.remove(info);
									}
            					});
            				} else {
            					showDialog("控制点正在使用中，无法删除",null);
            				}
            				
            				break ;
            			} 
            		}
            	}

                // List<ControlPointsInfo> tmpList =
                // ((ControlPointsActivity)c).list;
                // if(tmpList!=null){
                // AppCRTBApplication app=(AppCRTBApplication)
                // c.getApplicationContext();
                // final ControlPointsDaoImpl impl = new ControlPointsDaoImpl(c,
                // app.getCurrentWorkingFace().getProjectName());
                // mInfo=null;
                // boolean bCheck=false;
                // for(int i=0;i<tmpList.size();i++){
                // if(tmpList.get(i).isbCheck()){
                //
                // bCheck=true;
                // if(!tmpList.get(i).isbUse()){
                // mInfo=tmpList.get(i);
                // }
                // break;
                // }
                // }
                // if(!bCheck){
                // showDialog("请先选择要删除的控制点",null);
                // return;
                // }
                // if(mInfo==null){
                // showDialog("控制点正在使用中，无法删除",null);
                // return;
                // }
                // showDialog("删除后数据无法恢复，确定删除？", new dialogListener() {
                //
                // @Override
                // public void onClickOk() {
                // impl.DeleteStationInfo(mInfo.getId());
                // ((ControlPointsActivity)c).list.remove(mInfo);
                // ((ControlPointsActivity)c).adapter.notifyDataSetChanged();
                // showDialog("操作已成功",null);
                // }
                // });

                // }
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

    private void showDialog(String msgText, final dialogListener listener) {
        AlertDialog.Builder builder = new Builder(c);
        dlg = builder.create();

        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.dialog_custom_message);
        TextView message = (TextView)window.findViewById(R.id.message);
        message.setText(msgText);
        Button ok = (Button)window.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(dlg!=null){
                    dlg.dismiss();
                }
                if(listener!=null){
                    listener.onClickOk();
                }
            }
        });
    }

    public interface dialogListener{
        public void onClickOk();
    }
}
