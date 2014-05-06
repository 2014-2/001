package com.crtb.tunnelmonitor.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crtb.tunnelmonitor.WorkFlowActivity;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.RpcCallback;
import com.crtb.tunnelmonitor.utils.CrtbAppConfig;

public class ServerSettingActivity extends WorkFlowActivity {
	private static final String LOG_TAG = "ServerSettingActivity";
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
				login(mServerIp.getText().toString(), mUserName.getText().toString(), mPassword.getText().toString());
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
	
	private void login(final String serverAddress, final String userName, final String password) {
		CrtbWebService.getInstance().login(userName, password, new RpcCallback() {

			@Override
			public void onSuccess(Object[] data) {
				Log.d(LOG_TAG, "login success.");
				storeSettings(serverAddress, userName, password);
			}

			@Override
			public void onFailed(String reason) {
				Log.d(LOG_TAG, "login failed: " + reason);
			}
		});
	}
	
	private void storeSettings(String serverAddress, String userName, String password) {
		CrtbAppConfig appConfig = CrtbAppConfig.getInstance();
		appConfig.setServerAddress(serverAddress);
		appConfig.setUserName(userName);
		appConfig.setPassword(password);
	}
}
