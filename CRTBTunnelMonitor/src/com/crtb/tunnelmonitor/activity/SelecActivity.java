package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.entity.SurveyerInformation;
import com.crtb.tunnelmonitor.activity.R;
/**
 * 用户选择 界面
 *@author edison.xiao
 *@version 1.0
 */
public class SelecActivity extends Activity implements OnClickListener{

	/**本地用户点击*/
	private LinearLayout mLocalLayout;
	/**服务器用户点击*/
	private LinearLayout mServerLayout;
	/** 意图 */
	private Intent intent;
	
	private AppCRTBApplication mApp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select);
		initView();
		mApp = AppCRTBApplication.getInstance();
		mApp.getDatabase().ConnectDB();
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
		mLocalLayout = (LinearLayout) findViewById(R.id.local_layout);
		mServerLayout = (LinearLayout) findViewById(R.id.server_layout);
		// 点击事件
		mLocalLayout.setOnClickListener(this);
		mServerLayout.setOnClickListener(this);
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.local_layout:// 直接跳转主界面
			mApp.setbLocaUser(true);
			intent = new Intent(SelecActivity.this,MainActivity.class);
			intent.putExtra(Constant.LOGIN_TYPE, Constant.LOCAL_USER);
			startActivity(intent);
			break;
		case R.id.server_layout:
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
