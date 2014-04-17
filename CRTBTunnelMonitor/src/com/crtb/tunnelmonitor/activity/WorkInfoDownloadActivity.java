package com.crtb.tunnelmonitor.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.RpcCallback;
import com.crtb.tunnelmonitor.network.SectionStatus;

public class WorkInfoDownloadActivity extends Activity {
	private static final String LOG_TAG = "WorkInfoDownloadActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_workinfo_download);
		TextView title=(TextView) findViewById(R.id.tv_topbar_title);
        title.setText(R.string.download_work_data);
        downloadData(SectionStatus.VALID);
	}
	
	private void downloadData(SectionStatus status) {
		CrtbWebService.getInstance().getSectionCodeList(status, new RpcCallback() {
			
			@Override
			public void onSuccess(Object[] data) {
				Log.d(LOG_TAG, "download section code list success.");
			}
			
			@Override
			public void onFailed(String reason) {
				Log.d(LOG_TAG, "download section code list failed.");
			}
		});
	}
	
}
