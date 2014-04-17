package com.crtb.tunnelmonitor.activity;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionDao;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionInfo;
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
        downloadSectionCodeList(SectionStatus.VALID);
	}
	
	//下载断面编码数据
	private void downloadSectionCodeList(SectionStatus status) {
		CrtbWebService.getInstance().getSectionCodeList(status, new RpcCallback() {
			
			@Override
			public void onSuccess(Object[] data) {
				Log.d(LOG_TAG, "download section code list success.");
				downloadSectionList(Arrays.asList((String[])data));
			}
			
			@Override
			public void onFailed(String reason) {
				Log.d(LOG_TAG, "download section code list failed.");
			}
		});
	}
	
	//下载断面详细数据
	private void downloadSectionList(List<String> codeList) {
		for(String sectionCode : codeList) {
			downloadSection(sectionCode);
		}
	}
	
	private void downloadSection(String sectionCode) {
		CrtbWebService.getInstance().getSectionInfo(sectionCode, new RpcCallback() {
			
			@Override
			public void onSuccess(Object[] data) {
				TunnelCrossSectionInfo[] section = (TunnelCrossSectionInfo[])data;
				TunnelCrossSectionDao dao = TunnelCrossSectionDao.defaultDao();
				dao.insert(section[0]);
			}
			
			@Override
			public void onFailed(String reason) {
				// TODO Auto-generated method stub
				Log.d(LOG_TAG, "downloadSection failed: " + reason);
			}
		});
	}
}
