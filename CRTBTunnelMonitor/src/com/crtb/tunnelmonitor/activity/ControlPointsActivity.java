package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.crtb.tunnelmonitor.adapter.ControlPonitsListAdapter2;
import com.crtb.tunnelmonitor.dao.impl.v2.ControlPointsInfoDao;
import com.crtb.tunnelmonitor.entity.ControlPointsInfo;
import com.crtb.tunnelmonitor.utils.ConPopuWindow;

public class ControlPointsActivity extends Activity {
	/**
	 * 显示用户名和选中状态
	 */
	private ListView mContrlPointList;
	private int iItemPos = -1;
	public List<ControlPointsInfo> mControlPoints = null;
	public static ControlPonitsListAdapter2 mAdapter;
	private View vie;
	private ConPopuWindow menuWindow;
	
	private DialogInterface.OnClickListener itemsOnClick = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Log.d("ControlPointsActivity", "which = " + which);
		}
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_controlpoints);
		TextView title = (TextView) findViewById(R.id.tv_topbar_title);
		title.setText(R.string.control_point_manage);
		mContrlPointList = (ListView) findViewById(R.id.control_sonlist);
		loadData();
		/** 长按 */
		mContrlPointList
				.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						iItemPos = position;
						// 实例化对话
						new AlertDialog.Builder(ControlPointsActivity.this)
								.setItems(new String[] { "使用该点" },
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												switch (which) {
												case 0:
													ControlPointsInfo item = mControlPoints
															.get(iItemPos);
													item.setbUse("true");
													item.setbCheck("true");
													mControlPoints.set(
															iItemPos, item);
													for (int i = 0; i < mControlPoints
															.size(); i++) {
														if (i != iItemPos) {
															mControlPoints.get(
																	i).setbUse(
																	"false");
															mControlPoints
																	.get(i)
																	.setbCheck(
																			"false");
														}
													}
													ControlPointsInfoDao
															.defaultDao()
															.update(item);
													mAdapter.notifyDataSetChanged();
													break;
												}
											}
										}).setCancelable(false).show()
								.setCanceledOnTouchOutside(true);// 显示对话框
						return true;
					}
				});
		mContrlPointList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ControlPointsInfo item = mControlPoints.get(arg2);
				if ("false".equals(item.getbCheck())) {
					item.setbCheck("true");
					for (int i = 0; i < mControlPoints.size(); i++) {
						if (i != arg2) {
							mControlPoints.get(i).setbCheck("false");
						}
					}
				}
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	public void loadData() {
		mControlPoints = ControlPointsInfoDao.defaultDao()
				.queryAllControlPoints();
		if (mControlPoints == null) {
			mControlPoints = new ArrayList<ControlPointsInfo>();
		}
		mAdapter = new ControlPonitsListAdapter2(ControlPointsActivity.this,
				mControlPoints);
		mContrlPointList.setAdapter(mAdapter);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_MENU) {
				vie = new View(this);
				int num = 1;
				menuWindow = new ConPopuWindow(ControlPointsActivity.this,
						itemsOnClick, 1, 0);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			loadData();
			mAdapter.notifyDataSetChanged();
			break;

		default:
			break;
		}
	}
}
