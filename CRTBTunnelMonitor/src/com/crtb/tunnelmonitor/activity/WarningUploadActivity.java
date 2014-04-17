package com.crtb.tunnelmonitor.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class WarningUploadActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_warning_upload);
		TextView title=(TextView) findViewById(R.id.tv_topbar_title);
        title.setText(R.string.upload_warning_data);
	}

}
