package com.crtb.tunnelmonitor.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;

import com.crtb.tunnelmonitor.dao.impl.v2.RecordSubsidenceTotalDataDao;
import com.crtb.tunnelmonitor.entity.SubsidenceTotalData;

/**
 * 
 * @author zhouwei
 *
 */
public class CrtbRecordSubsidenceListView extends CrtbBaseListView {
	
	private CrtbRecordSubsidenceAdapter mAdapter ;
	
	public CrtbRecordSubsidenceListView(Context context) {
		this(context, null);
	}

	public CrtbRecordSubsidenceListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mAdapter	= new CrtbRecordSubsidenceAdapter(context);
		setAdapter(mAdapter);
		
		clearCacheColor() ;
	}

	public SubsidenceTotalData getItem(int position){
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
		List<SubsidenceTotalData> list = RecordSubsidenceTotalDataDao.defaultDao().queryAllSection();
		mAdapter.loadEntityDatas(list);
	}
	
}
