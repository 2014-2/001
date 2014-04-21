package com.crtb.tunnelmonitor.widget;

import java.util.ArrayList;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;

import com.crtb.tunnelmonitor.AppHandler;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;

/**
 * 
 * @author zhouwei
 *
 */
public final class SectionTunnelListView extends CrtbBaseListView {
	
	private SectionTunnelAdapter			mAdapter ;
	
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
	protected AppHandler getAppHandler() {
		return new AppHandler(getContext()){

			@SuppressWarnings("unchecked")
			@Override
			protected void dispose(Message msg) {
				
				if(msg.what == MSG_QUERY_SECTION_SUCCESS){
					mAdapter.loadEntityDatas( (ArrayList<TunnelCrossSectionIndex>)msg.obj);
				}
			}
		};
	}

	@Override
	public void onReload() {
		TunnelCrossSectionIndexDao.defaultDao().queryAllSection(mHandler) ;
	}

}
