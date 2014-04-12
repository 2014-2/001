package com.crtb.tunnelmonitor.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends Activity implements OnClickListener{

	private EditText mRegisterCode,mSerialNumber;
	
	private Button mOk,mCancel;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_layout);
		mRegisterCode=(EditText) findViewById(R.id.regist_code);
		mSerialNumber=(EditText) findViewById(R.id.serial_number);
	    mOk=(Button) findViewById(R.id.ok);
	    mCancel=(Button) findViewById(R.id.cancel);
	    mOk.setOnClickListener(this);
	    mCancel.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.ok:
			break;
		case R.id.cancel:
			RegisterActivity.this.finish();
			break;
		}	
	}


}
