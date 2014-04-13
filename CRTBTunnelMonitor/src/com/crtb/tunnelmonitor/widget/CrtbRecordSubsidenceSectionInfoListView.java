package com.crtb.tunnelmonitor.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;

import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionDao;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionInfo;

public class CrtbRecordSubsidenceSectionInfoListView extends CrtbBaseListView {
	
	private CrtbRecordSubsidenceSectionInfoAdapter mAdapter ;
	private String firstSection ;
	private boolean hasInit ;
	
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
	
	public SubsidenceCrossSectionInfo getItem(int position){
		return mAdapter.getItem(position);
	}
	
	public SubsidenceCrossSectionInfo getSelectedSection(){
		return mAdapter.getSelectedSection();
	}
	
	public void setFristSelected(String section){
		firstSection	= section ;
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

		if(list != null && !hasInit && firstSection != null){
			
			for(SubsidenceCrossSectionInfo item : list){
				if(item.getChainageName().equals(firstSection)){
					item.setUsed(true);
					hasInit = true ;
				}
			}
		}
		
		mAdapter.loadEntityDatas(list);
	}
	
}
