package com.crtb.tunnelmonitor.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;

import com.crtb.tunnelmonitor.AppHandler;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionIndexDao;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;

public class CrtbRecordSubsidenceSectionInfoListView extends CrtbBaseListView {
	
	private CrtbRecordSubsidenceSectionInfoAdapter mAdapter ;
	private List<String> sectionIds = new ArrayList<String>() ;
	
	public CrtbRecordSubsidenceSectionInfoListView(Context context) {
		this(context, null);
	}

	public CrtbRecordSubsidenceSectionInfoListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mAdapter	= new CrtbRecordSubsidenceSectionInfoAdapter(context);
		setAdapter(mAdapter);
		
		clearCacheColor() ;
		
		setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				mAdapter.changeStatus(position);
			}
		}) ;
	}
	
	public SubsidenceCrossSectionIndex getItem(int position){
		return mAdapter.getItem(position);
	}
	
	public String getSelectedSection(){
		return mAdapter.getSelectedSection();
	}
	
	public void setChainage(double value){
		mAdapter.setChainage(value);
	}
	
	public void setSectionIds(String section){
		
		if(section != null){
			
			String[] array = section.split(",");
			
			for(String id: array){
				sectionIds.add(id);
			}
		}
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
				case MSG_QUERY_SUBSIDENCE_SECTION_SUCCESS :
					
					List<SubsidenceCrossSectionIndex> list = (ArrayList<SubsidenceCrossSectionIndex>)msg.obj ;
					
					if(list != null && sectionIds != null){
						
						for(SubsidenceCrossSectionIndex item : list){
							
							for(String id : sectionIds){
								
								if(id.equals(String.valueOf(item.getGuid()))){
									item.setUsed(true);
								}
							}
						}
					}
					
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
	
}
