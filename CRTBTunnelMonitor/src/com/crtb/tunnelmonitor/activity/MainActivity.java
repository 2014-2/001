package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.List;

import org.zw.android.framework.util.StringUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.AppActivityManager;
import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.AppPreferences;
import com.crtb.tunnelmonitor.BaseActivity;
import com.crtb.tunnelmonitor.CommonObject;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.v2.ProjectIndexDao;
import com.crtb.tunnelmonitor.entity.TotalStationIndex;
import com.crtb.tunnelmonitor.entity.ProjectIndex;

/**
 * 主界面 创建时间：2014-3-18下午3:52:30
 */
public class MainActivity extends BaseActivity implements OnClickListener {

	public static final String KEY_CURRENT_WORKPLAN = "_key_current_workplan";

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

	public static List<TotalStationIndex> list = new ArrayList<TotalStationIndex>();
	private TotalStationIndex info = new TotalStationIndex();

	private ProjectIndex mCurrentWorkPlan;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();

		// clear all activity
		AppActivityManager.finishAllActivity();

		// current edit workplan
		mCurrentWorkPlan = ProjectIndexDao.defaultWorkPlanDao().queryEditWorkPlan();
		
		// remove current workplan from cache
		CommonObject.remove(KEY_CURRENT_WORKPLAN);

		// 测试
		info.setName("leon0");
		info.setInfo("未选中");
		info.setBaudRate(1);
		info.setDatabits(1);
		info.setID(1);
//		info.setParity(1);
		info.setCmd("cmd2");
		info.setStopbits(1);
		info.setTotalstationType("sa");
		list.add(info);
		info = new TotalStationIndex();
		info.setName("leon2");
		info.setInfo("未选中");
		info.setBaudRate(12);
		info.setDatabits(12);
		info.setID(12);
//		info.setParity(12);
		info.setCmd("cmd4");
		info.setStopbits(12);
		info.setTotalstationType("sa1");
		list.add(info);
	}

	/** 初始化控件 */
	private void initView() {
		
		TextView title = (TextView) findViewById(R.id.tv_topbar_title);
		String name = AppPreferences.getPreferences().getCurrentSimpleProjectName();
		title.setText(StringUtils.isEmpty(name) ? getString(R.string.main_title) : name);
		
		mWorkSection = (TextView) findViewById(R.id.worksection);
		mCrossSection = (TextView) findViewById(R.id.crosssection);
		mSheet = (TextView) findViewById(R.id.sheet);
		mStation = (TextView) findViewById(R.id.station);
		mMeasure = (TextView) findViewById(R.id.measure);
		mWarn = (TextView) findViewById(R.id.warn);
		mServer = (TextView) findViewById(R.id.server);
		mAbout = (TextView) findViewById(R.id.about);
		// 判断是否显示服务器图标
		int num = getIntent().getExtras().getInt(Constant.LOGIN_TYPE);
		if (num == Constant.LOCAL_USER) {
			// 影藏控件
			mServer.setVisibility(View.INVISIBLE);
			LinearLayout.LayoutParams param = (LayoutParams) mAbout
					.getLayoutParams();
			// param.width=
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
		AppCRTBApplication app = AppCRTBApplication.getInstance();
		if (app.isbLocaUser()) {
			mServer.setVisibility(View.INVISIBLE);

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mCurrentWorkPlan = ProjectIndexDao.defaultWorkPlanDao().queryEditWorkPlan();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.worksection:// 工作面
		{
			intent = new Intent(MainActivity.this, WorkActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.crosssection: // 断面

			if (mCurrentWorkPlan == null) {
				Toast.makeText(MainActivity.this, "请先打开工作面", 3000).show();
			} else {
				
				CommonObject.putObject(KEY_CURRENT_WORKPLAN, mCurrentWorkPlan);
				
				intent = new Intent(MainActivity.this, SectionActivity.class);
				startActivity(intent);
			}

			break;
		case R.id.sheet: // 记录单
			if (mCurrentWorkPlan == null) {
				Toast.makeText(MainActivity.this, "请先打开工作面", 3000).show();
			} else {
				
				CommonObject.putObject(KEY_CURRENT_WORKPLAN, mCurrentWorkPlan);
				
				intent = new Intent(MainActivity.this, RecordActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.station: // 全站仪

			if (mCurrentWorkPlan == null) {
				Toast.makeText(MainActivity.this, "请先打开工作面", 3000).show();
			} else {
				
				CommonObject.putObject(KEY_CURRENT_WORKPLAN, mCurrentWorkPlan);
				
				intent = new Intent(MainActivity.this,
						TotalStationActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.measure: // 测量
			if (mCurrentWorkPlan == null) {
				Toast.makeText(MainActivity.this, "请先打开工作面", 3000).show();
			} else {
				
				CommonObject.putObject(KEY_CURRENT_WORKPLAN, mCurrentWorkPlan);
				
				intent = new Intent(MainActivity.this, TestRecordActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.warn: // 预警
			if (mCurrentWorkPlan == null) {
				Toast.makeText(MainActivity.this, "请先打开工作面", 3000).show();
			} else {
				intent = new Intent(MainActivity.this, WarningActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.server: // 服务器
			intent = new Intent(MainActivity.this, ServersActivity.class);
			startActivity(intent);
			break;
		case R.id.about: // 关于
			intent = new Intent(MainActivity.this, AsregardsActivity.class);
			startActivity(intent);
			break;
		}

	}
}
