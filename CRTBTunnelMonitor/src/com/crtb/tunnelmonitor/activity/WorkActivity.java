package com.crtb.tunnelmonitor.activity;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;
import org.zw.android.framework.ioc.InjectView;

import android.os.Bundle;

import com.crtb.tunnelmonitor.WorkFlowActivity;
import com.crtb.tunnelmonitor.widget.CrtbWorkPlanListView;

/**
 * work plan
 * 
 * @author zhouwei
 *
 */
@InjectLayout(layout=R.layout.activity_work)
public final class WorkActivity extends WorkFlowActivity {
	
	@InjectView(id=R.id.list_view_workplans)
	private CrtbWorkPlanListView mListView ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// add by wei.zhou
		InjectCore.injectUIProperty(this);

		// title
		setTopbarTitle(getString(R.string.work_plan_title));
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		mListView.onResume() ;
	}
}
