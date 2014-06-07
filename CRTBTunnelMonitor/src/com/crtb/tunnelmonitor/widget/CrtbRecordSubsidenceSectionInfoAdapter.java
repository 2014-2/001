package com.crtb.tunnelmonitor.widget;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;
import org.zw.android.framework.ioc.InjectView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

/**
 * 
 * @author zhouwei
 *
 */
public class CrtbRecordSubsidenceSectionInfoAdapter extends CrtbEntityAdapter<SubsidenceCrossSectionIndex> {

	private double defChainage = 0 ;
	
	protected CrtbRecordSubsidenceSectionInfoAdapter(Context context) {
		super(context);
	}

	protected void changeStatus(int position){
		
		for(int index = 0 ,size = mList.size() ; index < size ; index++){
			
			SubsidenceCrossSectionIndex item = mList.get(index) ;
			
			if(index == position){
				item.setUsed(!item.isUsed());
			}
		}
		
		notifyDataSetChanged() ;
	}
	
	public void setChainage(double value){
		defChainage	= value ;
		notifyDataSetChanged() ;
	}
	
	protected String getSelectedSection(){
		
		StringBuilder str = new StringBuilder() ;
		boolean inster = false ;
		
		for(SubsidenceCrossSectionIndex item : mList){
			
			if(item.isUsed()){
				
				if(inster){
					str.append(",");
				}
				
				str.append(item.getGuid());
				
				inster	= true ;
			}
		}
		
		return str.toString() ;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		HolderView holder 					= null ;
		SubsidenceCrossSectionIndex item 	= getItem(position);
		
		if(convertView == null){
			holder		= new HolderView() ;
			convertView	= InjectCore.injectOriginalObject(holder);
			convertView.setTag(holder);
		} else {
			holder		= (HolderView)convertView.getTag() ;
		}
		
		holder.chainage.setText(item.getSectionName());
		holder.distance.setText(String.valueOf(Math.abs(CrtbUtils.formatDouble(defChainage - item.getChainage()))));
		
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
