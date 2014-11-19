package com.crtb.tunnelmonitor.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
	/** 断面封存 */
	private TextView mSectionStop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_servers);
		initView();
		// 获取当前工作面
        ProjectIndex currentProject = ProjectIndexDao.defaultWorkPlanDao()
                .queryEditWorkPlan();
        if (currentProject == null) {
            Toast.makeText(getApplicationContext(), "请先打开工作面",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        // 判断工点是否已经下载
        List<WorkSiteIndex> originalWorkSiteList = WorkSiteIndexDao
                .defaultDao().queryAllWorkSite();
        if (originalWorkSiteList == null || originalWorkSiteList.size() < 1) {
            Toast.makeText(getApplicationContext(), "请先下载工点数据",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // 判断是否关联工点
        SiteProjectMapping mapping = SiteProjectMappingDao.defaultDao()
                .queryOneByProjectId(currentProject.getId());
        if (mapping == null) {
            Toast.makeText(getApplicationContext(), "请先关联工点数据",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // 获取工点信息、并保存到CrtbWebService中工点信息
        WorkSiteIndex workSiteIndex = WorkSiteIndexDao.defaultDao()
                .queryWorkSiteById(mapping.getWorkSiteId());
        if (workSiteIndex == null) {
            Toast.makeText(getApplicationContext(), "请重新关联工点数据",
                    Toast.LENGTH_SHORT).show();
            // 清空当前工点信息
            CrtbWebService.getInstance().setZoneCode("");
            CrtbWebService.getInstance().setSiteCode("");
        } else {
            // 保存当前工点信息
            CrtbWebService.getInstance().setZoneCode(
                    workSiteIndex.getZoneCode());
            CrtbWebService.getInstance().setSiteCode(
                    workSiteIndex.getSiteCode());
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
		mSectionStop = (TextView) findViewById(R.id.section_stop);
		mParameter.setOnClickListener(this);
		mDataUpload.setOnClickListener(this);
		mWarnUpload.setOnClickListener(this);
		mWorkInfoDownload.setOnClickListener(this);
		mSectionStop.setOnClickListener(this);
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
		case R.id.section_stop:
			JumpSectionStop();
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
		//Intent intent = new Intent(this, WarningUploadActivity.class);
		Intent intent = new Intent(this, WarningShowSortBySectionActivity.class);
		startActivity(intent);
	}

	private void JumpWorkInfoDownload() {
		Intent intent = new Intent(this, WorkInfoDownloadActivity.class);
		startActivity(intent);
	}
	
	private void JumpSectionStop() {
		Intent intent = new Intent(this,SectionStopActivity.class);
		startActivity(intent);		
	}

}
