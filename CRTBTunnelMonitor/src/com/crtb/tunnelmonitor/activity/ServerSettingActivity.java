package com.crtb.tunnelmonitor.activity;

import org.zw.android.framework.ioc.InjectLayout;
import org.zw.android.framework.ioc.InjectView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crtb.tunnelmonitor.WorkFlowActivity;

@InjectLayout(layout=R.layout.server_setting_layout)
public class ServerSettingActivity extends WorkFlowActivity {
    @InjectView(id=R.id.server_ip)
	private EditText mServerIp;
    
    @InjectView(id=R.id.username)
    private EditText mUserName;
    
    @InjectView(id=R.id.password)
    private EditText mPassword;
    
    @InjectView(id=R.id.ok)
    private Button mOk;
    
    @InjectView(id=R.id.cancel)
    private Button mCancel;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server_setting_layout);
		init();
	}

	private void init(){
		TextView title=(TextView) findViewById(R.id.tv_topbar_title);
		title.setText(R.string.server_setting);
	}
	
	
}
