package com.sxlc.activity;

import java.util.ArrayList;
import java.util.List;

import com.crtb.tssurveyprovider.Coordinate3D;
import com.crtb.tssurveyprovider.TSConnectType;
import com.crtb.tssurveyprovider.TSSurveyProvider;
import com.sxlc.adapter.ControlPonitsListAdapter;
import com.sxlc.common.Constant;
import com.sxlc.dao.impl.RecordDaoImpl;
import com.sxlc.dao.impl.TotalStationDaoImpl;
import com.sxlc.entity.RecordInfo;
import com.sxlc.entity.SubsidenceCrossSectionInfo;
import com.sxlc.entity.TotalStationInfo;
import com.sxlc.entity.TunnelCrossSectionInfo;
import com.sxlc.entity.WorkInfos;
import com.sxlc.utils.SonPopupWindow;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class StationActivity extends Activity {
	/**
	 * 显示用户名和选中状态
	 */
	private ListView listview;
	private Intent intent;
	public static ControlPonitsListAdapter adapter;
	int iListPos = -1;
	private OnClickListener itemsOnClick;
	private View vie;

	private SonPopupWindow menuWindow;
	public List<TotalStationInfo> list = null;
	private CRTBTunnelMonitor CurApp = null;
	private int iConnectType = 0;
	private boolean bConnect = false;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sta);
		CurApp = ((CRTBTunnelMonitor)getApplicationContext());
		init();
		getadapter();
		/** 长按 */
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				iListPos = position;
				// 实例化对话
				new AlertDialog.Builder(StationActivity.this)
						.setItems(/* items */Constant.ControlPointsItems,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										iConnectType = which;
										switch (which) {
										case 0: // 蓝牙连接
											diagolyes();
											break;
										case 1:// 串口连接
											diagolyes();
											break;
										case 2:// 断开连接
											diagolduanyes();
										default:
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
				TotalStationInfo item = list.get(arg2);	
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
	
	public boolean Connect() {
//		TSConnectType tstype = TSConnectType.Bluetooth;
//        String [] tsParams = new String[] {"9600"};
//        
//        int nret = TSSurveyProvider.getDefaultAdapter().BeginConnection(tstype,tsParams );
//     
//        Coordinate3D point = new Coordinate3D();
//        try {
//        	nret = TSSurveyProvider.getDefaultAdapter().GetCoord(0, 0, point);
//        	String text = String.format("%1$s,%2$s,%3$s", point.x,point.y,point.z);
//        	tv.setText(text);
//				}
//				catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//	        nret = TSSurveyProvider.getDefaultAdapter().EndConnection();
//	        TSSurveyProvider.getDefaultAdapter().TestConnection();
//	  		}
		return true;
	}
	
	public boolean DisConnect() {
		return true;
	}
	public void setdata() {
		WorkInfos CurW = CurApp.GetCurWork();
		if(CurW == null)
		{
			return;
		}
		list = CurW.getTsList();
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
				list = new ArrayList<TotalStationInfo>();
			}
			TotalStationDaoImpl impl = new TotalStationDaoImpl(this, CurW.getProjectName());
			impl.GetTotalStationList(list);
			CurW.setTsList(list);
			CurApp.UpdateWork(CurW);
		}
	}

	private void init() {
		listview = (ListView) findViewById(R.id.lv_conlist);
	}

	public void getadapter() {
//		list = new ArrayList<TotalStationInfo>();
//		list = MainActivity.list;
		setdata();
		adapter = new ControlPonitsListAdapter(StationActivity.this, list);
		listview.setAdapter(adapter);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			// TODO Auto-generated method stub
			if (keyCode == 82) {
				vie = new View(this);
				int num = 1;
				menuWindow = new SonPopupWindow(this, itemsOnClick, 1, 0);
				menuWindow.showAtLocation(vie, Gravity.BOTTOM
						| Gravity.CENTER_HORIZONTAL, 0, 0);
			}
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				this.finish();
			}
		}
		return true;
	}

	/** 连接全站仪失败 */
	private void diagolno() {
		AlertDialog dlg = new AlertDialog.Builder(StationActivity.this)
				.create();
		dlg.show();
		Window window = dlg.getWindow();
		window.setContentView(R.layout.connectdialog);
		TextView text = (TextView) window.findViewById(R.id.connertexr);
		text.setText("连接" + list.get(iListPos).getName() + "全站仪成功");
	}

	/** 连接全站仪成功 */
	private void diagolyes() {

		bConnect = Connect();
		
		AlertDialog dlg = new AlertDialog.Builder(StationActivity.this)
				.create();
		dlg.show();
		Window window = dlg.getWindow();
		window.setContentView(R.layout.connectyesdialog);
		TextView text = (TextView) window.findViewById(R.id.connertexryes);
		String sTmp;
		if (bConnect) {
			sTmp = "成功";
		}
		else {
			sTmp = "失败";
		}
		text.setText("连接" + list.get(iListPos).getName() + "全站仪"+sTmp);
	}

	/** 断开全站仪成功 */
	private void diagolduanyes() {
		DisConnect();
		AlertDialog dlg = new AlertDialog.Builder(StationActivity.this)
				.create();
		dlg.show();
		Window window = dlg.getWindow();
		window.setContentView(R.layout.connectyesdialog);
		TextView text = (TextView) window.findViewById(R.id.connertexryes);
		text.setText("断开连接" + list.get(iListPos).getName() + "全站仪成功");
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
