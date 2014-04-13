package com.crtb.tunnelmonitor.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.crtb.tunnelmonitor.AppCRTBApplication;
/**
 * 关于
 *@author edison.xiao
 *@since JDK1.6
 *@version 1.0
 */
public class AsregardsActivity extends Activity implements OnClickListener{
	private TextView mUserInfo;
	private TextView mRegister;
	private TextView mUpdate;
	private TextView mUserBook;
	private TextView mSoftwareInfo;
	
	
	private AppCRTBApplication app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_asregards);
		TextView title=(TextView) findViewById(R.id.tv_topbar_title);
		title.setText(R.string.about);
		
		mUserInfo=(TextView) findViewById(R.id.userinfo);
		app=AppCRTBApplication.getInstance();
		if(app.isbLocaUser()){
			mUserInfo.setVisibility(View.GONE);
		}
		mUserInfo.setOnClickListener(this);
		mRegister=(TextView) findViewById(R.id.register);
		mRegister.setOnClickListener(this);
		mUpdate=(TextView) findViewById(R.id.update);
		mUpdate.setOnClickListener(this);
		mUserBook=(TextView) findViewById(R.id.userbook);
		mUserBook.setOnClickListener(this);
		mSoftwareInfo=(TextView) findViewById(R.id.software_info);
		mSoftwareInfo.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.update:
			JumpUpdate();
			break;
		case R.id.userbook:
			JumpUserBook();
			break;
		case R.id.userinfo:
			JumpUserInfo();
			break;
		case R.id.software_info:
			JumpSoftwareInfo();
			break;
		case R.id.register:
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
		Intent intent=new Intent(this,UserBookActivity.class);
		startActivity(intent);

	}
	
	private void JumpUpdate(){
		Intent intent=new Intent(this,SoftwareUpdateActivity.class);
		startActivity(intent);

	}
}
