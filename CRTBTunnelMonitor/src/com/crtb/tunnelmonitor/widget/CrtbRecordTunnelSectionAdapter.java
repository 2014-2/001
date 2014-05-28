package com.crtb.tunnelmonitor.widget;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;
import org.zw.android.framework.ioc.InjectView;
import org.zw.android.framework.util.DateUtils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.dao.impl.v2.ProjectIndexDao;
import com.crtb.tunnelmonitor.entity.ProjectIndex;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

public class CrtbRecordTunnelSectionAdapter extends CrtbEntityAdapter<RawSheetIndex> {
	
	private String prefix ;

	protected CrtbRecordTunnelSectionAdapter(Context context) {
		super(context);
		
		ProjectIndex bean = ProjectIndexDao.defaultWorkPlanDao().queryEditWorkPlan() ;
		
		prefix	= bean != null ? bean.getChainagePrefix() : "";
	}
	
	protected boolean isNewestRawSheetIndex(RawSheetIndex bean){
		
		if(mList.isEmpty()){
			return false ;
		}
		
		return mList.indexOf(bean) == 0 ;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		HolderView holder 		= null ;
		RawSheetIndex item 		= getItem(position);
		
		if(convertView == null){
			
			holder		= new HolderView() ;
			convertView = InjectCore.injectOriginalObject(holder);
			convertView.setTag(holder);
		} else {
			holder	= (HolderView)convertView.getTag() ;
		}
		
		holder.chainage.setText(DateUtils.toDateString(item.getCreateTime(),DateUtils.DATE_TIME_FORMAT));
		holder.excavation.setText(CrtbUtils.formatSectionName(prefix, item.getFACEDK()));
		
		return convertView;
	}

	@InjectLayout(layout=R.layout.item_record_tunnel_section_layout)
	class HolderView {
		
		@InjectView(id=R.id.t1)
		TextView chainage ;
		
		@InjectView(id=R.id.t2)
		TextView excavation ;
	}
}
