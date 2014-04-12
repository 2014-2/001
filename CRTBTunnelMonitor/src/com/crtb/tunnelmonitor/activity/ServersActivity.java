package com.crtb.tunnelmonitor.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
/**
 * 服务器
 */
public class ServersActivity extends Activity implements OnClickListener{

	/**意图*/
	private Intent intent;
	/**参数设置"*/
	private TextView mParameter;
	/**数据上传"*/
	private TextView mDataUpload;
	/**预警上传"*/
	private TextView mWarnUpload;
	/**下载工作面"*/
	private TextView mWorkInfoDownload;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_servers);
	    initView();
	}
	
	/** 初始化控件 */
	private void initView() {
        TextView title=(TextView) findViewById(R.id.tv_topbar_title);
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
			break;
		case R.id.warn_upload:
			break;
		case R.id.workinfo_download:
			break;
		default:
			break;
		}
		
	}
	
	private void JumpParameter(){
		Intent intent=new Intent(this,ServerSettingActivity.class);
		startActivity(intent);
	}
}
