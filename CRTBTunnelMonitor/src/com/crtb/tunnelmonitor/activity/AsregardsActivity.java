package com.crtb.tunnelmonitor.activity;

import com.crtb.tunnelmonitor.CRTBTunnelMonitor;
import com.crtb.tunnelmonitor.activity.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
/**
 * 关于
 *@author edison.xiao
 *@since JDK1.6
 *@version 1.0
 */
public class AsregardsActivity extends Activity implements OnClickListener{
	private RelativeLayout mUserInfo,mRegist,mUpdate,mUserBook,mSoftwareInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_asregards);
		mUserInfo=(RelativeLayout) findViewById(R.id.user_info);
		CRTBTunnelMonitor app=(CRTBTunnelMonitor)getApplicationContext();
		if(app.isbLocaUser()){
			mUserInfo.setVisibility(View.GONE);
		}
		mUserInfo.setOnClickListener(this);
		mRegist=(RelativeLayout) findViewById(R.id.regist);
		mRegist.setOnClickListener(this);
		mUpdate=(RelativeLayout) findViewById(R.id.update);
		mUpdate.setOnClickListener(this);
		mUserBook=(RelativeLayout) findViewById(R.id.user_book);
		mUserBook.setOnClickListener(this);
		mSoftwareInfo=(RelativeLayout) findViewById(R.id.soft_info);
		mSoftwareInfo.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.update:
			
			break;
		case R.id.user_book:
			JumpUserBook();
			break;
		case R.id.user_info:
			JumpUserInfo();
			break;
		case R.id.soft_info:
			JumpSoftwareInfo();
			break;
		case R.id.regist:
			JumpRegister();
			break;
		}
	}
    
	private void JumpUserInfo(){
		Intent intent=new Intent(this,UserInfoActivity.class);
		startActivity(intent);
	}
	
	private void JumpSoftwareInfo(){
		Intent intent=new Intent(this,SoftwareInfoActivity.class);
		startActivity(intent);
	}
	
	private void JumpRegister(){
		Intent intent=new Intent(this,RegisterActivity.class);
		startActivity(intent);
	}
	
	private void JumpUserBook(){
		
	}
}
