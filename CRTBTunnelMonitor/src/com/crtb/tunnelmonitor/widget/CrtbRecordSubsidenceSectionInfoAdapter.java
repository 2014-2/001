package com.crtb.tunnelmonitor.widget;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;
import org.zw.android.framework.ioc.InjectView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionInfo;

/**
 * 
 * @author zhouwei
 *
 */
public class CrtbRecordSubsidenceSectionInfoAdapter extends CrtbEntityAdapter<SubsidenceCrossSectionInfo> {

	protected CrtbRecordSubsidenceSectionInfoAdapter(Context context) {
		super(context);
	}

	protected void changeStatus(int position){
		
		for(int index = 0 ,size = mList.size() ; index < size ; index++){
			
			SubsidenceCrossSectionInfo item = mList.get(index) ;
			
			if(index == position){
				item.setUsed(!item.isUsed());
			} else {
				item.setUsed(false);
			}
		}
		
		notifyDataSetChanged() ;
	}
	
	protected SubsidenceCrossSectionInfo getSelectedSection(){
		
		for(SubsidenceCrossSectionInfo item : mList){
			
			if(item.isUsed()){
				return item ;
			}
		}
		
		return null ;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		HolderView holder 					= null ;
		SubsidenceCrossSectionInfo item 	= getItem(position);
		
		if(convertView == null){
			holder		= new HolderView() ;
			convertView	= InjectCore.injectOriginalObject(holder);
			convertView.setTag(holder);
		} else {
			holder		= (HolderView)convertView.getTag() ;
		}
		
		holder.chainage.setText(item.getChainageName());
		
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
