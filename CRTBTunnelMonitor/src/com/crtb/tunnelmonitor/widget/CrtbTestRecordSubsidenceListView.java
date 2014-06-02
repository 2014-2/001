package com.crtb.tunnelmonitor.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;

import com.crtb.tunnelmonitor.dao.impl.v2.RawSheetIndexDao;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;

/**
 * 下沉断面测量单
 * 
 * @author zhouwei
 *
 */
public final class CrtbTestRecordSubsidenceListView extends CrtbBaseListView {
	
	private CrtbTestRecordSubsidenceAdapter mAdapter ;
	
	public CrtbTestRecordSubsidenceListView(Context context) {
		this(context, null);
	}

	public CrtbTestRecordSubsidenceListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mAdapter	= new CrtbTestRecordSubsidenceAdapter(context);
		setAdapter(mAdapter);
		
		clearCacheColor() ;
	}
	
	public void changeStatus(RawSheetIndex obj){
		mAdapter.changeStatus(obj);
	}
	
	public RawSheetIndex getItem(int position){
		return mAdapter.getItem(position);
	}
	
	public List<RawSheetIndex> getSelectedSection(){
		return mAdapter.getSelectedSection();
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
		
		List<RawSheetIndex> list = RawSheetIndexDao.defaultDao().queryAllSubsidenceSectionRawSheetIndex();
		mAdapter.loadEntityDatas(list);
	}
	
}
