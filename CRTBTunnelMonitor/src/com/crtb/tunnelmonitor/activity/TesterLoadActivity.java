package com.crtb.tunnelmonitor.activity;

import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.service.CrtbWebService;
import com.crtb.tunnelmonitor.service.RpcCallback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class TesterLoadActivity extends Activity implements OnClickListener{

	private Button mLoad;
	
	private EditText mUserName;
	
	private EditText mPassword;
	
	private RelativeLayout mTvLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download_tester_layout);
		mLoad=(Button) findViewById(R.id.load);
		mUserName=(EditText) findViewById(R.id.username);
		mPassword=(EditText) findViewById(R.id.password);
		mTvLayout=(RelativeLayout) findViewById(R.id.tv_layout);
		mTvLayout.setVisibility(View.GONE);
		mLoad.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.load:
			String name = mUserName.getText().toString().trim();
			String pwd = mPassword.getText().toString().trim();
			if(TextUtils.isEmpty(name)||TextUtils.isEmpty(pwd)){
				Toast.makeText(this, "请填写用户名和密码", Toast.LENGTH_LONG).show();
			    return;
			}
			login(name, pwd);
			break;
		}
	}

	
    private void login(String username,String password){
    	CrtbWebService.getInstance().login(username, password, new RpcCallback() {

    		@Override
    		public void onSuccess(Object[] data) {
    			Toast.makeText(TesterLoadActivity.this, "获取成功",Toast.LENGTH_LONG).show();
    		}

    		

			@Override
			public void onFailed(String reason) {
				Toast.makeText(TesterLoadActivity.this, "用户名或密码错误", Toast.LENGTH_LONG).show();
			}
    		});
    }
	
   
}
