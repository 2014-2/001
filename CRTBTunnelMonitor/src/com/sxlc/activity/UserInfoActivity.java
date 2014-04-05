package com.sxlc.activity;


import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

/**
 * 
 * @author edison.xiao
 * @since 2014.4.5
 */
public class UserInfoActivity extends Activity {

	private EditText mName,mIdCard,mNote;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_info_layout);
		mName=(EditText) findViewById(R.id.name);
		mIdCard=(EditText) findViewById(R.id.idcard);
		mNote=(EditText) findViewById(R.id.note);
	}
          
}
