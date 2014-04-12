package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.adapter.ControlPonitsListAdapter2;
import com.crtb.tunnelmonitor.dao.impl.ControlPointsDaoImpl;
import com.crtb.tunnelmonitor.entity.ControlPointsInfo;
import com.crtb.tunnelmonitor.entity.WorkInfos;
import com.crtb.tunnelmonitor.utils.ConPopuWindow;
/**
 * 
 * @author edison.xiao
 * 
 * @since 2014.4.5
 */
public class ControlPointsActivity extends Activity {
	/**
	 * 显示用户名和选中状态
	 */
	private ListView listview;
	private int iItemPos = -1;
	public List<ControlPointsInfo> list = null;
	public static ControlPonitsListAdapter2 adapter;
	private DialogInterface.OnClickListener itemsOnClick;
	private View vie;
	private ConPopuWindow menuWindow;
	private AppCRTBApplication CurApp = null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_controlpoints);
		TextView title=(TextView) findViewById(R.id.tv_topbar_title);
		title.setText(R.string.control_point_manage);
		CurApp = ((AppCRTBApplication)getApplicationContext());
		listview=(ListView) findViewById(R.id.control_sonlist);
		getadapter();
		/** 长按 */
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				iItemPos = position; 
				// 实例化对话
				new AlertDialog.Builder(ControlPointsActivity.this)
						.setItems(new String[]{"使用该点"},
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										switch (which) {
										case 0: 
											ControlPointsInfo item = list.get(iItemPos);
											item.setbUse(true);
											item.setbCheck(true);
											list.set(iItemPos, item);
											for (int i = 0; i < list.size(); i++) {
												if (i != iItemPos) {
													list.get(i).setbUse(false);
													list.get(i).setbCheck(false);
												}
											}
											WorkInfos curW = CurApp.GetCurWork();
											curW.setCpList(list);
											adapter.notifyDataSetChanged();
											break;
										}
									}
								}).setCancelable(false).show()
						.setCanceledOnTouchOutside(true);// 显示对话框
				return true;
			}
		});
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				ControlPointsInfo item = list.get(arg2);	
				boolean bCheck = !item.isbCheck();
				item.setbCheck(!item.isbCheck());
				list.set(arg2, item);
				if (bCheck) {
					for (int i = 0; i < list.size(); i++) {
						if (i != arg2) {
							list.get(i).setbCheck(false);
						}
					}
				}
				adapter.notifyDataSetChanged();
			}
		});
	}
	public void setdata() {
		WorkInfos CurW = CurApp.GetCurWork();
		if(CurW == null)
		{
			return;
		}
		list = CurW.getCpList();
		boolean bLoadDB = true;
		if(list!=null)
		{
			if(list.size()>0)
			{
				bLoadDB = false;
			}
		}
		if(bLoadDB)
		{
			if(list == null)
			{
				list = new ArrayList<ControlPointsInfo>();
			}
			ControlPointsDaoImpl impl = new ControlPointsDaoImpl(this, CurW.getProjectName());
			impl.GetControlPointsList(list);
			CurW.setCpList(list);
			CurApp.UpdateWork(CurW);
		}
	}
	
	public void getadapter() {
		setdata();
		adapter = new ControlPonitsListAdapter2(ControlPointsActivity.this, list);
		listview.setAdapter(adapter);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			// TODO Auto-generated method stub
			if (keyCode == KeyEvent.KEYCODE_MENU) {
				vie = new View(this);
				int num = 1;
				menuWindow = new ConPopuWindow(ControlPointsActivity.this, itemsOnClick, 1, 0);
				menuWindow.showAtLocation(vie, Gravity.BOTTOM
						| Gravity.CENTER_HORIZONTAL, 0, 0);
			}
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				this.finish();
			}
		}
		return true;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (resultCode) {
		case RESULT_OK:
			adapter.notifyDataSetChanged();
			break;

		default:
			break;
		}
	}
}
