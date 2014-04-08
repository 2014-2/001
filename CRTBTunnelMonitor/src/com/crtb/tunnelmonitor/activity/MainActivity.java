package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.entity.TotalStationInfo;
import com.crtb.tunnelmonitor.entity.WorkInfos;
import com.crtb.tunnelmonitor.activity.R;

/**
 * 主界面 创建时间：2014-3-18下午3:52:30

 */
public class MainActivity extends Activity implements OnClickListener {

	/** 工作面 */
	private TextView mWorkSection;
	/** 断面 */
	private TextView mCrossSection;
	/** 记录单 */
	private TextView mSheet;
	/** 全站仪 */
	private TextView mStation;
	/** 测量 */
	private TextView mMeasure;
	/** 预警 */
	private TextView mWarn;
	/** 服务器 */
	private TextView mServer;
	/** 关于 */
	private TextView mAbout;
	/** 意图 */
	private Intent intent;
	/**
	 * 用户名和选中状态
	 */
	public static List<TotalStationInfo> list = new ArrayList<TotalStationInfo>();
	private TotalStationInfo info = new TotalStationInfo();
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		//测试
		info.setName("leon0");
		info.setInfo("未选中");
		info.setBaudRate(1);
		info.setDatabits(1);
		info.setId(1);
		info.setParity(1);
		info.setCmd("cmd2");
		info.setStopbits(1);
		info.setTotalstationType("sa");
		list.add(info);
		info = new TotalStationInfo();
		info.setName("leon2");
		info.setInfo("未选中");
		info.setBaudRate(12);
		info.setDatabits(12);
		info.setId(12);
		info.setParity(12);
		info.setCmd("cmd4");
		info.setStopbits(12);
		info.setTotalstationType("sa1");
		list.add(info);
	}

	/** 初始化控件 */
	private void initView() {
		mWorkSection = (TextView) findViewById(R.id.worksection);
		mCrossSection = (TextView) findViewById(R.id.crosssection);
		mSheet = (TextView) findViewById(R.id.sheet);
		mStation = (TextView) findViewById(R.id.station);
		mMeasure = (TextView) findViewById(R.id.measure);
		mWarn = (TextView) findViewById(R.id.warn);
		mServer = (TextView) findViewById(R.id.server);
		mAbout = (TextView) findViewById(R.id.about);
		// 判断是否显示服务器图标
		int num = getIntent().getExtras().getInt(Constant.Select_LoginName_Name);
		if (num == Constant.Select_LoginValue_Local) {
			// 影藏控件
			mServer.setVisibility(View.INVISIBLE);
			LinearLayout.LayoutParams param=(LayoutParams) mAbout.getLayoutParams();
		    //param.width=
		}
		// 点击事件
		mWorkSection.setOnClickListener(this);
		mCrossSection.setOnClickListener(this);
		mSheet.setOnClickListener(this);
		mStation.setOnClickListener(this);
		mMeasure.setOnClickListener(this);
		mWarn.setOnClickListener(this);
		mServer.setOnClickListener(this);
		mAbout.setOnClickListener(this);
		AppCRTBApplication app=(AppCRTBApplication)getApplicationContext();
		if(app.isbLocaUser()){
			mServer.setVisibility(View.INVISIBLE);
			
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		 case R.id.worksection://工作面
		 {
			 intent = new Intent(MainActivity.this,WorkActivity.class);
			 startActivity(intent);
		 }
		 break;
		 case R.id.crosssection: // 断面
		 {
			AppCRTBApplication CurApp = ((AppCRTBApplication)getApplicationContext());
			WorkInfos CurW = CurApp.GetCurWork();
			if(CurW == null)
			{
				Toast.makeText(MainActivity.this, "请先打开工作面", 3000).show();
				return;
			}
			intent = new Intent(MainActivity.this,SectionActivity.class);
			startActivity(intent);
		 }
		 break;
		 case R.id.sheet: // 记录单
		 {
				AppCRTBApplication CurApp = ((AppCRTBApplication)getApplicationContext());
				WorkInfos CurW = CurApp.GetCurWork();
				if(CurW == null)
				{
					Toast.makeText(MainActivity.this, "请先打开工作面", 3000).show();
					return;
				}
    			intent = new Intent(MainActivity.this,RecordActivity.class);
				startActivity(intent);
		 }
		 break;
		 case R.id.station: // 全站仪
		 {
			AppCRTBApplication CurApp = ((AppCRTBApplication)getApplicationContext());
			WorkInfos CurW = CurApp.GetCurWork();
			if(CurW == null)
			{
				Toast.makeText(MainActivity.this, "请先打开工作面", 3000).show();
				return;
			}
			 intent = new Intent(MainActivity.this,TotalStationActivity.class);
			 startActivity(intent);
		 }
		 break;
		 case R.id.measure: // 测量
		 {
			AppCRTBApplication CurApp = ((AppCRTBApplication)getApplicationContext());
			WorkInfos CurW = CurApp.GetCurWork();
			if(CurW == null)
			{
				Toast.makeText(MainActivity.this, "请先打开工作面", 3000).show();
				return;
			}
			 intent = new Intent(MainActivity.this,TestRecordActivity.class);
			 startActivity(intent);
		 }
		 break;
		 case R.id.warn: // 预警
		 {
			AppCRTBApplication CurApp = ((AppCRTBApplication)getApplicationContext());
			WorkInfos CurW = CurApp.GetCurWork();
			if(CurW == null)
			{
				Toast.makeText(MainActivity.this, "请先打开工作面", 3000).show();
				return;
			}
			intent = new Intent(MainActivity.this,WarningActivity.class);
			startActivity(intent);
		 }
		 break;
		 case R.id.server: // 服务器
		 {
			 intent = new Intent(MainActivity.this,ServersActivity.class);
			 startActivity(intent);
		 }
		 break;
		 case R.id.about: // 关于
		 {
			 intent = new Intent(MainActivity.this,AsregardsActivity.class);
			 startActivity(intent);
		 }
		 break;
		}

	}
}
