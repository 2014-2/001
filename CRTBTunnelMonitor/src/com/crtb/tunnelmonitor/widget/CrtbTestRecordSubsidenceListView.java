package com.crtb.tunnelmonitor.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;

import com.crtb.tunnelmonitor.dao.impl.v2.RecordSubsidenceTotalDataDao;
import com.crtb.tunnelmonitor.entity.SubsidenceTotalData;

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
		
		setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mAdapter.changeStatus(position);
			}
		}) ;
	}
	
	public SubsidenceTotalData getItem(int position){
		return mAdapter.getItem(position);
	}
	
	public SubsidenceTotalData getSelectedSection(){
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
		
		List<SubsidenceTotalData> list = RecordSubsidenceTotalDataDao.defaultDao().queryAllSection() ;
		mAdapter.loadEntityDatas(list);
	}
	
}
