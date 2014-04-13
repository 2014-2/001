package com.crtb.tunnelmonitor.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;

import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionDao;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionInfo;

/**
 * 
 * @author zhouwei
 *
 */
public class CrtbRecordTunnelSectionInfoListView extends CrtbBaseListView {
	
	private CrtbRecordTunnelSectionInfoAdapter mAdapter ;
	private String firstSection ;
	private boolean hasInit ;
	
	public CrtbRecordTunnelSectionInfoListView(Context context) {
		this(context, null);
	}

	public CrtbRecordTunnelSectionInfoListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mAdapter	= new CrtbRecordTunnelSectionInfoAdapter(context);
		setAdapter(mAdapter);
		
		clearCacheColor() ;
		
		setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				mAdapter.changeStatus(position);
			}
		}) ;
	}
	
	public TunnelCrossSectionInfo getItem(int position){
		return mAdapter.getItem(position);
	}
	
	public TunnelCrossSectionInfo getSelectedSection(){
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
		
		List<TunnelCrossSectionInfo> list = TunnelCrossSectionDao.defaultDao().queryAllSection() ;
		
		if(list != null && !hasInit && firstSection != null){
			
			for(TunnelCrossSectionInfo item : list){
				if(item.getChainageName().equals(firstSection)){
					item.setUsed(true);
					hasInit = true ;
				}
			}
		}
		
		mAdapter.loadEntityDatas(list);
	}
	
}
