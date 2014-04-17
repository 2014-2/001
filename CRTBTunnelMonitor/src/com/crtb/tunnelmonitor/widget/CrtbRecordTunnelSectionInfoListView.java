package com.crtb.tunnelmonitor.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;

import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

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
	
	public TunnelCrossSectionIndex getItem(int position){
		return mAdapter.getItem(position);
	}
	
	public TunnelCrossSectionIndex getSelectedSection(){
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
		
		List<TunnelCrossSectionIndex> list = TunnelCrossSectionIndexDao.defaultDao().queryAllSection() ;
		
		if(list != null && !hasInit && firstSection != null){
			
			for(TunnelCrossSectionIndex item : list){
				
				// 断面名称
				String name = CrtbUtils.formatSectionName(item.getChainagePrefix(), (float)item.getChainage()) ;
				
				if(name.equals(firstSection)){
					item.setUsed(true);
					hasInit = true ;
				}
			}
		}
		
		mAdapter.loadEntityDatas(list);
	}
	
}
