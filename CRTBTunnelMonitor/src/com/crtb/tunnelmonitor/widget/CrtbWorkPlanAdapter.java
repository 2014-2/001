package com.crtb.tunnelmonitor.widget;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;
import org.zw.android.framework.ioc.InjectView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.entity.ProjectIndex;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

/**
 * WorkPlan Adapter
 * 
 * @author zhouwei
 *
 */
public final class CrtbWorkPlanAdapter extends CrtbEntityAdapter<ProjectIndex> {

	protected CrtbWorkPlanAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final ProjectIndex item = getItem(position);
		HolderView holder 	= null ;
		
		if(convertView == null){
			
			holder		= new HolderView() ;
			convertView	= InjectCore.injectOriginalObject(holder);
			
			convertView.setTag(holder);
		} else {
			holder	= (HolderView)convertView.getTag() ;
		}
		
		holder.workplanName.setText(item.getProjectName());
		holder.startMileage.setText(CrtbUtils.formatSectionName(item.getChainagePrefix(),(float)item.getStartChainage()));
		holder.endMileage.setText(CrtbUtils.formatSectionName(item.getChainagePrefix(),(float)item.getEndChainage()));
		
		return convertView;
	}

	@InjectLayout(layout=R.layout.item_workplan_layout)
	private class HolderView {
		
		@InjectView(id=R.id.tv_workplan_name)
		TextView workplanName ;
		
		@InjectView(id=R.id.tv_workplan_start_mileage)
		TextView startMileage ;
		
		@InjectView(id=R.id.tv_workplan_end_mileage)
		TextView endMileage ;
	}
}
