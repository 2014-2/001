package com.crtb.tunnelmonitor;


import android.os.Bundle;

/**
 * Workflow activity
 * 
 * @author zhouwei
 *
 */
public class WorkFlowActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// add activity to manager
		AppActivityManager.addActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		// remove activity from manager
		AppActivityManager.removeActivity(this);
	}
	
}
