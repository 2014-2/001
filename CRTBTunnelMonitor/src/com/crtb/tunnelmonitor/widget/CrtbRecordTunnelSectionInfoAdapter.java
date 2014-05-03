package com.crtb.tunnelmonitor.widget;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;
import org.zw.android.framework.ioc.InjectView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;

/**
 * 
 * @author zhouwei
 *
 */
public final class CrtbRecordTunnelSectionInfoAdapter extends CrtbEntityAdapter<TunnelCrossSectionIndex> {

	private double defaultChainage = 0 ;
	
	protected CrtbRecordTunnelSectionInfoAdapter(Context context) {
		super(context);
	}
	
	protected void setChainage(double value){
		defaultChainage	= value ;
		notifyDataSetChanged() ;
	}

	protected void changeStatus(int position){
		
		for(int index = 0 ,size = mList.size() ; index < size ; index++){
			
			TunnelCrossSectionIndex item = mList.get(index) ;
			
			if(index == position){
				item.setUsed(!item.isUsed());
			}
		}
		
		notifyDataSetChanged() ;
	}
	
	protected String getSelectedSection(){
		
		StringBuilder str = new StringBuilder() ;
		boolean insert = false ;
		
		for(TunnelCrossSectionIndex item : mList){
			
			if(item.isUsed()){
				
				if(insert){
					str.append(",");
				}
				
				str.append(item.getID());

				insert = true ;
			}
		}
		
		return str.toString() ;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		HolderView holder 			= null ;
		TunnelCrossSectionIndex item = getItem(position);
		
		if(convertView == null){
			holder		= new HolderView() ;
			convertView	= InjectCore.injectOriginalObject(holder);
			convertView.setTag(holder);
		} else {
			holder		= (HolderView)convertView.getTag() ;
		}
		
		holder.chainage.setText(item.getSectionName());
		holder.distance.setText(String.valueOf(defaultChainage - item.getChainage()));
		
		if(item.isUsed()){
			holder.status.setBackgroundResource(R.drawable.use);
		} else {
			holder.status.setBackgroundResource(R.drawable.nouse);
		}
		
		return convertView;
	}

	@InjectLayout(layout=R.layout.item_record_section_info_layout)
	class HolderView {
		
		@InjectView(id=R.id.t1)
		TextView chainage ;
		
		@InjectView(id=R.id.t2)
		TextView distance ;
		
		@InjectView(id=R.id.t3)
		TextView status ;
	}
}
