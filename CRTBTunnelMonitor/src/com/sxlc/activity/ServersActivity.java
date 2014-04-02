package com.sxlc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
/**
 * 服务器
 *创建时间：2014-3-16上午10:38:01
 *@author 张涛
 *@since JDK1.6
 *@version 1.0
 */
public class ServersActivity extends Activity implements OnClickListener{

	/**意图*/
	private Intent intent;
	/**参数设置"*/
	private RelativeLayout rl_parameter;
	/**数据上传"*/
	private RelativeLayout rl_datauploading;
	/**预警上传"*/
	private RelativeLayout rl_warninguploading;
	/**下载工作面"*/
	private RelativeLayout rl_downloadwork;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_servers);
	    initView();
	}
	/** 初始化控件 */
	private void initView() {
		rl_parameter = (RelativeLayout) findViewById(R.id.rl_parameter);
		rl_datauploading = (RelativeLayout) findViewById(R.id.rl_datauploading);
		rl_warninguploading = (RelativeLayout) findViewById(R.id.rl_warninguploading);
		rl_downloadwork = (RelativeLayout) findViewById(R.id.rl_downloadwork);
		// 
		rl_parameter.setOnClickListener(this);
		rl_datauploading.setOnClickListener(this);
		rl_warninguploading.setOnClickListener(this);
		rl_downloadwork.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		default:
			break;
		}
		
	}
}
