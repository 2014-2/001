package com.sxlc.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.sxlc.common.Constant;
import com.sxlc.entity.SurveyerInformation;
/**
 * 用户选择 界面
 *@author edison.xiao
 *@since JDK1.6
 *@version 1.0
 */
public class SelecActivity extends Activity implements OnClickListener{

	/**本地用户点击*/
	private ImageView select_img_place;
	/**服务器用户点击*/
	private ImageView select_img_service;
	/** 意图 */
	private Intent intent;
	
	private CRTBTunnelMonitor mApp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select);
		initView();
		mApp = ((CRTBTunnelMonitor)getApplicationContext());
		mApp.GetDB().ConnectDB();
		/*测试代码*/
		List<SurveyerInformation> testList = new ArrayList<SurveyerInformation>();
		SurveyerInformation test = new SurveyerInformation();
		test.setCertificateID("511325197812155213");
		test.setId(1);
		test.setInfo("测试");
		test.setProjectID(1);
		test.setSurveyerName("测试员");
		testList.add(test);
		mApp.setPersonList(testList);
		mApp.setCurPerson(test);
		/*测试代码*/
	}

	/** 初始化控件 */
	private void initView() {
		select_img_place = (ImageView) findViewById(R.id.select_img_place);
		select_img_service = (ImageView) findViewById(R.id.select_img_service);
		// 点击事件
		select_img_place.setOnClickListener(this);
		select_img_service.setOnClickListener(this);
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.select_img_place:// 直接跳转主界面
			mApp.setbLocaUser(true);
			intent = new Intent(SelecActivity.this,MainActivity.class);
			intent.putExtra(Constant.Select_LoginName_Name, Constant.Select_LoginValue_Local);
			startActivity(intent);
			break;
		case R.id.select_img_service: // 跳转服务的登录界面
			intent = new Intent(SelecActivity.this,LoginActivity.class);
			startActivity(intent);
			mApp.setbLocaUser(false);
			break;
		default:
			break;
		}
		SelecActivity.this.finish();
	}

}
