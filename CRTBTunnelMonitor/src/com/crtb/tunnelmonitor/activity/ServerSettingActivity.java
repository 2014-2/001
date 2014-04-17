package com.crtb.tunnelmonitor.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.WorkFlowActivity;
import com.crtb.tunnelmonitor.utils.CrtbAppConfig;

public class ServerSettingActivity extends WorkFlowActivity {
	private EditText mServerIp;
    private EditText mUserName;
    private EditText mPassword;
    private Button mOk;
    private Button mCancel;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server_setting_layout);
		init();
		mServerIp = (EditText) findViewById(R.id.server_ip);
		mUserName = (EditText) findViewById(R.id.username);
		mPassword = (EditText) findViewById(R.id.password);
		mOk = (Button) findViewById(R.id.ok);
		mOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CrtbAppConfig appConfig = CrtbAppConfig.getInstance(getApplicationContext());
				appConfig.setServerAddress(mServerIp.getText().toString());
				appConfig.setUserName(mUserName.getText().toString());
				appConfig.setPassword(mPassword.getText().toString());
				Toast.makeText(ServerSettingActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
				finish();
			}
		});
		mCancel = (Button) findViewById(R.id.cancel);
		mCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void init(){
		TextView title=(TextView) findViewById(R.id.tv_topbar_title);
		title.setText(R.string.server_setting);
	}
	
}
