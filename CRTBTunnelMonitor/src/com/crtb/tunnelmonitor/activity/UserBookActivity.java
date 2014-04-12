package com.crtb.tunnelmonitor.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class UserBookActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userbook_layout);
		init();
	}

	private void init(){
		TextView title=(TextView) findViewById(R.id.tv_topbar_title);
		title.setText(R.string.user_book);
	}
	
}
