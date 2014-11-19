package com.crtb.tunnelmonitor.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;

import com.crtb.tunnelmonitor.AppHandler;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionIndexDao;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;

public class WarningSortBySectionSubsidenceListView extends CrtbBaseListView {
	
	private SectionSubsidenceAdapter 	mAdapter ;

	public WarningSortBySectionSubsidenceListView(Context context) {
		this(context, null);
	}
	
	public WarningSortBySectionSubsidenceListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mAdapter	= new SectionSubsidenceAdapter(context);
		setAdapter(mAdapter);
		
		clearCacheColor() ;
	}
	
	public void loadData(ArrayList<String> sectionGuids){
		String secGuids = "";
		String sql = "";
		if(sectionGuids != null && sectionGuids.size() > 0){
			for(String item : sectionGuids){
				secGuids += "\"" + item + "\",";
			}
			secGuids = secGuids.substring(0, secGuids.length() -1);
			sql = " SELECT * FROM SubsidenceCrossSectionIndex WHERE Guid in ("+secGuids+")"
			    + " ORDER BY Chainage ASC" ;
		}
		 
		SubsidenceCrossSectionIndexDao.defaultDao().queryAllSectionBySql(mHandler,sql) ;
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
		//SubsidenceCrossSectionIndexDao.defaultDao().queryAllSection(mHandler);
	}

	public SubsidenceCrossSectionIndex getItem(int position){
		return mAdapter.getItem(position);
	}

}
