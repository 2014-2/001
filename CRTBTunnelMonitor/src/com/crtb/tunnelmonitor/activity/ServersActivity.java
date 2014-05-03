package com.crtb.tunnelmonitor.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.dao.impl.v2.ProjectIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SiteProjectMappingDao;
import com.crtb.tunnelmonitor.dao.impl.v2.WorkSiteIndexDao;
import com.crtb.tunnelmonitor.entity.ProjectIndex;
import com.crtb.tunnelmonitor.entity.SiteProjectMapping;
import com.crtb.tunnelmonitor.entity.WorkSiteIndex;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.RpcCallback;
import com.crtb.tunnelmonitor.task.DataDownloadManager;
import com.crtb.tunnelmonitor.task.DataDownloadManager.DownloadListener;
import com.crtb.tunnelmonitor.utils.CrtbAppConfig;

/**
 * 服务器
 */
public class ServersActivity extends Activity implements OnClickListener {
	private static final String LOG_TAG = "ServersActivity";

	/** 意图 */
	private Intent intent;
	/** 参数设置" */
	private TextView mParameter;
	/** 数据上传" */
	private TextView mDataUpload;
	/** 预警上传" */
	private TextView mWarnUpload;
	/** 下载工作面" */
	private TextView mWorkInfoDownload;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_servers);
		initView();
		// 服务器相关所有操作都必须先打开工作面
		ProjectIndex currentProject = ProjectIndexDao.defaultWorkPlanDao().queryEditWorkPlan();
		if (currentProject == null) {
			Toast.makeText(getApplicationContext(), "请先打开工作面", Toast.LENGTH_SHORT).show();
			finish();
		} else {
			if (!CrtbWebService.getInstance().isLogined()) {
				login();
			}
			SiteProjectMapping mapping = SiteProjectMappingDao.defaultDao().queryOneByProjectId(currentProject.getId());
			if (mapping == null) {
				Toast.makeText(getApplicationContext(), "请先下载工点数据", Toast.LENGTH_SHORT).show();
			} else {
				WorkSiteIndex workSiteIndex = WorkSiteIndexDao.defaultDao().queryWorkSiteById(mapping.getWorkSiteId());
				if (workSiteIndex == null) {
					Toast.makeText(getApplicationContext(), "未查询到关联的工点数据", Toast.LENGTH_SHORT).show();
				} else {
					CrtbWebService.getInstance().setZoneCode(workSiteIndex.getZoneCode());
					CrtbWebService.getInstance().setSiteCode(workSiteIndex.getSiteCode());
				}
			}
		}
	}

	/** 初始化控件 */
	private void initView() {
		TextView title = (TextView) findViewById(R.id.tv_topbar_title);
		title.setText(R.string.server_interact);

		mParameter = (TextView) findViewById(R.id.parameter);
		mDataUpload = (TextView) findViewById(R.id.data_upload);
		mWarnUpload = (TextView) findViewById(R.id.warn_upload);
		mWorkInfoDownload = (TextView) findViewById(R.id.workinfo_download);
		//
		mParameter.setOnClickListener(this);
		mDataUpload.setOnClickListener(this);
		mWarnUpload.setOnClickListener(this);
		mWorkInfoDownload.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.parameter:
			JumpParameter();
			break;
		case R.id.data_upload:
			JumpDataUpload();
			break;
		case R.id.warn_upload:
			JumpWarningUpload();
			break;
		case R.id.workinfo_download:
			JumpWorkInfoDownload();
			break;
		default:
			break;
		}

	}

	private void JumpParameter() {
		Intent intent = new Intent(this, ServerSettingActivity.class);
		startActivity(intent);
	}

	private void JumpDataUpload() {
		Intent intent = new Intent(this, DataUploadActivity.class);
		startActivity(intent);
	}

	private void JumpWarningUpload() {
		Intent intent = new Intent(this, WarningUploadActivity.class);
		startActivity(intent);
	}

	private void JumpWorkInfoDownload() {
		Intent intent = new Intent(this, WorkInfoDownloadActivity.class);
		startActivity(intent);
	}

	private void login() {
		CrtbAppConfig config = CrtbAppConfig.getInstance();
		String userName = config.getUserName();
		String password = config.getPassword();
		if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
			Toast.makeText(this, "未设置服务器参数", Toast.LENGTH_SHORT).show();
			return;
		}
		CrtbWebService.getInstance().login(userName, password, new RpcCallback() {

			@Override
			public void onSuccess(Object[] data) {
				Log.d(LOG_TAG, "login success.");
			}

			@Override
			public void onFailed(String reason) {
				Log.d(LOG_TAG, "login failed: " + reason);
			}
		});
	}

//	private void downloadZoneAndSiteCode() {
//		CrtbWebService.getInstance().getZoneAndSiteCode(new RpcCallback() {
//
//			@Override
//			public void onSuccess(Object[] data) {
//				Log.d(LOG_TAG, "download zone code and site code success.");
//			}
//
//			@Override
//			public void onFailed(String reason) {
//				Log.d(LOG_TAG, "download zone code and site code failed.");
//			}
//		});
//	}

}
