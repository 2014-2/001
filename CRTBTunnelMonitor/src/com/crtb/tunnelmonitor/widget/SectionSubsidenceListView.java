package com.crtb.tunnelmonitor.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;

import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionIndexDao;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;

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
		List<SubsidenceCrossSectionIndex>  list = SubsidenceCrossSectionIndexDao.defaultDao().queryAllSection();
		mAdapter.loadEntityDatas(list);
	}

	public SubsidenceCrossSectionIndex getItem(int position){
		return mAdapter.getItem(position);
	}

}
