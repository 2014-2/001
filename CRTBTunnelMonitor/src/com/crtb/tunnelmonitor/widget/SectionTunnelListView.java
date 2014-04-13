package com.crtb.tunnelmonitor.widget;

import java.util.List;

import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionDao;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionInfo;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 
 * @author zhouwei
 *
 */
public final class SectionTunnelListView extends CrtbBaseListView {
	
	private SectionTunnelAdapter	mAdapter ;
	
	public SectionTunnelListView(Context context) {
		this(context, null);
	}

	public SectionTunnelListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mAdapter	= new SectionTunnelAdapter(context);
		setAdapter(mAdapter);
		
		clearCacheColor() ;
	}
	
	public TunnelCrossSectionInfo getItem(int position){
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
		List<TunnelCrossSectionInfo> list = TunnelCrossSectionDao.defaultDao().queryAllSection() ;
		mAdapter.loadEntityDatas(list);
	}

}
