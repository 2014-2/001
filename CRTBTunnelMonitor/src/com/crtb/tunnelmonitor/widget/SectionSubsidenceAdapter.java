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

public class SectionSubsidenceAdapter extends CrtbEntityAdapter<SubsidenceCrossSectionIndex> {

	protected SectionSubsidenceAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		HolderView holder 					= null ;
		SubsidenceCrossSectionIndex item 	= getItem(position);
		
		if(convertView == null){
			
			holder		= new HolderView() ;
			convertView = InjectCore.injectOriginalObject(holder);
			convertView.setTag(holder);
		} else {
			holder	= (HolderView)convertView.getTag() ;
		}
		
		holder.chainage.setText(item.getSectionName());
		holder.excavation.setText(item.isHasTestData() ? "有" : "无" );
		
		return convertView;
	}

	@InjectLayout(layout=R.layout.item_tunnel_section_layout)
	class HolderView {
		
		@InjectView(id=R.id.t1)
		TextView chainage ;
		
		@InjectView(id=R.id.t2)
		TextView excavation ;
	}
}
