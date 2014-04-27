package com.crtb.tunnelmonitor.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;

import com.crtb.tunnelmonitor.AppHandler;
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
	protected AppHandler getAppHandler() {
		return new AppHandler(getContext()){

			@SuppressWarnings("unchecked")
			@Override
			protected void dispose(Message msg) {
				
				switch(msg.what){
				case MSG_QUERY_SUBSIDENCE_SECTION_FAILED :
					
					break ;
				case MSG_QUERY_SUBSIDENCE_SECTION_SUCCESS :
					
					List<SubsidenceCrossSectionIndex> list = (ArrayList<SubsidenceCrossSectionIndex>)msg.obj ;
					
					mAdapter.loadEntityDatas(list);
					
					break ;
				}
			}
			
		};
	}

	@Override
	public void onReload() {
		SubsidenceCrossSectionIndexDao.defaultDao().queryAllSection(mHandler);
	}

	public SubsidenceCrossSectionIndex getItem(int position){
		return mAdapter.getItem(position);
	}

}
