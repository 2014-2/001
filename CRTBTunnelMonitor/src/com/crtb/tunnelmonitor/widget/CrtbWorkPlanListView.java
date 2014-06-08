package com.crtb.tunnelmonitor.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;

import com.crtb.tunnelmonitor.dao.impl.v2.ProjectIndexDao;
import com.crtb.tunnelmonitor.entity.ProjectIndex;

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

	public CrtbWorkPlanListView(final Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mAdapter	= new CrtbWorkPlanAdapter(context);
		setAdapter(mAdapter);
		
		clearCacheColor() ;
	}

	public void onReload(){
		
		List<ProjectIndex> list = ProjectIndexDao.defaultWorkPlanDao().queryAllWorkPlan() ;
		mAdapter.loadEntityDatas(list);
	}
	
	public ProjectIndex getItem(int position){
		return mAdapter.getItem(position);
	}

	@Override
	public void onResume() {
		
		if(mAdapter.isEmpty()){
			onReload();
		} else {
			mAdapter.notifyDataSetChanged() ;
		}
	}
	
	
}
