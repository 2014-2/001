package com.crtb.tunnelmonitor.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;

import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionDao;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionInfo;

public class SectionSubsidenceListView extends CrtbBaseListView {
	
	private SectionSubsidenceAdapter 	mAdapter ;

	public SectionSubsidenceListView(Context context) {
		this(context, null);
	}
	
	public SectionSubsidenceListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mAdapter	= new SectionSubsidenceAdapter(context);
		setAdapter(mAdapter);
		
		clearCacheColor() ;
	}
	
	@Override
	public void onResume() {
		
		if(mAdapter.isEmpty()){
			onReload();
		} else {
			mAdapter.notifyDataSetChanged() ;
		}
	}
	
	@Override
	public void onReload() {
		List<SubsidenceCrossSectionInfo>  list = SubsidenceCrossSectionDao.defaultDao().queryAllSection();
		mAdapter.loadEntityDatas(list);
	}

	public SubsidenceCrossSectionInfo getItem(int position){
		return mAdapter.getItem(position);
	}

}
