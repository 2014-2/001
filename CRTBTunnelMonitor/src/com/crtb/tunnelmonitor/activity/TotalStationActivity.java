package com.crtb.tunnelmonitor.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TotalStationActivity extends Activity {
	/**
	 * 全站仪连接管理
	 */
	private LinearLayout mStation;
	/**
	 * 控点管理
	 */
	private LinearLayout mControlPoint;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_totalstation);
		init();
	}
	private void init() {
		TextView title=(TextView) findViewById(R.id.tv_topbar_title);
		title.setText(R.string.total_station);
		mStation=(LinearLayout) findViewById(R.id.station);
		mStation.setOnClickListener(listener);
		mControlPoint=(LinearLayout) findViewById(R.id.control_point);
		mControlPoint.setOnClickListener(listener);
	}
	private OnClickListener listener = new OnClickListener() {
		
		public void onClick(View v) {
			Intent intent;
			switch(v.getId()){
				case R.id.station://全站仪连接管理
					intent = new Intent(TotalStationActivity.this,StationActivity.class);
					startActivity(intent);
					break;
				case R.id.control_point://控点管理管理
					intent = new Intent(TotalStationActivity.this,ControlPointsActivity.class);
					startActivity(intent);
					break;
			}
			
		}
	};
}
