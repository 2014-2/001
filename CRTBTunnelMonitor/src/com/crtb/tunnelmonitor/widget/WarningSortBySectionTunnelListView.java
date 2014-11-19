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
public final class WarningSortBySectionTunnelListView extends CrtbBaseListView {
	
	private SectionTunnelAdapter			mAdapter ;
	
	public WarningSortBySectionTunnelListView(Context context) {
		this(context, null);
	}

	public WarningSortBySectionTunnelListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mAdapter	= new SectionTunnelAdapter(context);
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
			sql = " SELECT * FROM TunnelCrossSectionIndex WHERE Guid in ("+ secGuids +")"
			    + " ORDER BY Chainage ASC ";
		}
		 
		TunnelCrossSectionIndexDao.defaultDao().queryAllSectionBySql(mHandler,sql) ;
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
		//TunnelCrossSectionIndexDao.defaultDao().queryAllSectionBySql(mHandler) ;
	}

}
