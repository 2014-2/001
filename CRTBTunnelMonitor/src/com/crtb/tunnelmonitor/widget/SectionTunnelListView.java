package com.crtb.tunnelmonitor.widget;

import java.util.List;

import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;

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
	
	public TunnelCrossSectionIndex getItem(int position){
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
		List<TunnelCrossSectionIndex> list = TunnelCrossSectionIndexDao.defaultDao().queryAllSection() ;
		mAdapter.loadEntityDatas(list);
	}

}
