package com.crtb.tunnelmonitor.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class WorkInfoDownloadActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_workinfo_download);
		TextView title=(TextView) findViewById(R.id.tv_topbar_title);
        title.setText(R.string.download_work_data);
	}
}
