package com.sxlc.activity;

import java.util.ArrayList;
import java.util.List;

import com.sxlc.common.Constant;
import com.sxlc.entity.TotalStationInfo;
import com.sxlc.entity.WorkInfos;


import com.sxlc.infors.ProjectInformation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 主界面 创建时间：2014-3-18下午3:52:30
 * 
 * @author 张涛
 * @since JDK1.6
 * @version 1.0
 */
public class MainActivity extends Activity implements OnClickListener {

	/** 工作面 */
	private RelativeLayout main_rl_work;
	/** 断面 */
	private RelativeLayout main_rl_fracturesurface;
	/** 记录单 */
	private RelativeLayout main_rl_record;
	/** 全站仪 */
	private RelativeLayout main_rl_total;
	/** 测量 */
	private RelativeLayout main_rl_measure;
	/** 预警 */
	private RelativeLayout main_rl_warning;
	/** 服务器 */
	private RelativeLayout main_rl_servers;
	/** 关于 */
	private RelativeLayout main_rl_asregards;
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
		main_rl_work = (RelativeLayout) findViewById(R.id.main_rl_work);
		main_rl_fracturesurface = (RelativeLayout) findViewById(R.id.main_rl_fracturesurface);
		main_rl_record = (RelativeLayout) findViewById(R.id.main_rl_record);
		main_rl_total = (RelativeLayout) findViewById(R.id.main_rl_total);
		main_rl_measure = (RelativeLayout) findViewById(R.id.main_rl_measure);
		main_rl_warning = (RelativeLayout) findViewById(R.id.main_rl_warning);
		main_rl_servers = (RelativeLayout) findViewById(R.id.main_rl_servers);
		main_rl_asregards = (RelativeLayout) findViewById(R.id.main_rl_asregards);
		// 判断是否显示服务器图标
		int num = getIntent().getExtras().getInt(Constant.Select_LoginName_Name);
		if (num == Constant.Select_LoginValue_Local) {
			// 影藏控件
			main_rl_servers.setVisibility(View.GONE);
		}
		// 点击事件
		main_rl_work.setOnClickListener(this);
		main_rl_fracturesurface.setOnClickListener(this);
		main_rl_record.setOnClickListener(this);
		main_rl_total.setOnClickListener(this);
		main_rl_measure.setOnClickListener(this);
		main_rl_warning.setOnClickListener(this);
		main_rl_servers.setOnClickListener(this);
		main_rl_asregards.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		 case R.id.main_rl_work://工作面
		 {
			 intent = new Intent(MainActivity.this,WorkActivity.class);
			 startActivity(intent);
		 }
		 break;
		 case R.id.main_rl_fracturesurface: // 断面
		 {
			CRTBTunnelMonitor CurApp = ((CRTBTunnelMonitor)getApplicationContext());
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
		 case R.id.main_rl_record: // 记录单
		 {
				CRTBTunnelMonitor CurApp = ((CRTBTunnelMonitor)getApplicationContext());
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
		 case R.id.main_rl_total: // 全站仪
		 {
			CRTBTunnelMonitor CurApp = ((CRTBTunnelMonitor)getApplicationContext());
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
		 case R.id.main_rl_measure: // 测量
		 {
			CRTBTunnelMonitor CurApp = ((CRTBTunnelMonitor)getApplicationContext());
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
		 case R.id.main_rl_warning: // 预警
		 {
			CRTBTunnelMonitor CurApp = ((CRTBTunnelMonitor)getApplicationContext());
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
		 case R.id.main_rl_servers: // 服务器
		 {
			 intent = new Intent(MainActivity.this,ServersActivity.class);
			 startActivity(intent);
		 }
		 break;
		 case R.id.main_rl_asregards: // 关于
		 {
			 intent = new Intent(MainActivity.this,AsregardsActivity.class);
			 startActivity(intent);
		 }
		 break;
		}

	}
}
