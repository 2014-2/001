package com.crtb.tunnelmonitor.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;

import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionDao;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionInfo;

public class CrtbRecordSubsidenceSectionInfoListView extends CrtbBaseListView {
	
	private CrtbRecordSubsidenceSectionInfoAdapter mAdapter ;
	
	public CrtbRecordSubsidenceSectionInfoListView(Context context) {
		this(context, null);
	}

	public CrtbRecordSubsidenceSectionInfoListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mAdapter	= new CrtbRecordSubsidenceSectionInfoAdapter(context);
		setAdapter(mAdapter);
		
		clearCacheColor() ;
	}
	
	public SubsidenceCrossSectionInfo getItem(int position){
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

	@Override
	public void onReload() {
		List<SubsidenceCrossSectionInfo> list = SubsidenceCrossSectionDao.defaultDao().queryAllSection();
		mAdapter.loadEntityDatas(list);
	}
	
}
