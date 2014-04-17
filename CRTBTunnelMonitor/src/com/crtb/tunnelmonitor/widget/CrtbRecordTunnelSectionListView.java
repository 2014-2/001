package com.crtb.tunnelmonitor.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;

import com.crtb.tunnelmonitor.dao.impl.v2.RecordTunnelSettlementTotalDataDao;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;

public class CrtbRecordTunnelSectionListView extends CrtbBaseListView {
	
	private CrtbRecordTunnelSectionAdapter mAdapter ;
	
	public CrtbRecordTunnelSectionListView(Context context) {
		this(context, null);
	}

	public CrtbRecordTunnelSectionListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mAdapter	= new CrtbRecordTunnelSectionAdapter(context);
		setAdapter(mAdapter);
		
		clearCacheColor() ;
	}
	
	public TunnelSettlementTotalData getItem(int position){
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
		List<TunnelSettlementTotalData> list = RecordTunnelSettlementTotalDataDao.defaultDao().queryAllTunnelSection() ;
		mAdapter.loadEntityDatas(list);
	}
	
}
