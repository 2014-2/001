package com.crtb.tunnelmonitor.widget;

import java.util.List;

import com.crtb.tunnelmonitor.dao.impl.v2.WorkPlanDao;
import com.crtb.tunnelmonitor.entity.WorkPlan;
import com.crtb.tunnelmonitor.utils.DateUtils;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 
 * @author zhouwei
 *
 */
public final class CrtbWorkPlanListView extends CrtbBaseListView {
	
	private CrtbWorkPlanAdapter 		mAdapter ;
	
	public CrtbWorkPlanListView(Context context){
		this(context, null);
	}

	public CrtbWorkPlanListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mAdapter	= new CrtbWorkPlanAdapter(context);
		setAdapter(mAdapter);
		
		clearCacheColor() ;
	}

	public void onReload(){
		
		List<WorkPlan> list = WorkPlanDao.defaultWorkPlanDao().queryAllWorkPlan() ;
		mAdapter.loadEntityDatas(list);
	}

	@Override
	public void onResume() {
		
//		WorkPlan wp = new WorkPlan() ;
//		wp.setWorkPlanName("成都红星路隧道1#");
//		wp.setMileagePrefix("CDHX");
//		wp.setCreationTime(DateUtils.toDateString(DateUtils.getCurrtentTimes()));
//		wp.setStartMileage(0.0f);
//		wp.setEndMileage(100.0f);
//		wp.setConstructionOrganization("成都路桥建设工程有限公司");
//		
//		WorkPlanDao.defaultWorkPlanDao().insert(wp);
		
		if(mAdapter.isEmpty()){
			onReload();
		} else {
			mAdapter.notifyDataSetChanged() ;
		}
	}
	
	
}
