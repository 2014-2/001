package com.sxlc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

public class TotalStationActivity extends Activity {
	/**
	 * 全站仪连接管理
	 */
	private RelativeLayout total;
	/**
	 * 控点管理
	 */
	private RelativeLayout conter;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_totalstation);
		init();
	}
	private void init() {
		total=(RelativeLayout) findViewById(R.id.total_qzy);
		total.setOnClickListener(listener);
		conter=(RelativeLayout) findViewById(R.id.total_kd);
		conter.setOnClickListener(listener);
	}
	private OnClickListener listener = new OnClickListener() {
		
		public void onClick(View v) {
			Intent intent;
			switch(v.getId()){
				case R.id.total_qzy://全站仪连接管理
					intent = new Intent(TotalStationActivity.this,StationActivity.class);
					startActivity(intent);
					break;
				case R.id.total_kd://控点管理管理
					intent = new Intent(TotalStationActivity.this,ControlPointsActivity.class);
					startActivity(intent);
					break;
			}
			
		}
	};
}
