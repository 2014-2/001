package com.crtb.tunnelmonitor.widget;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;
import org.zw.android.framework.ioc.InjectView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

public final class CrtbTestRecordSearchAdapter extends CrtbEntityAdapter<RawSheetIndex> {

	protected CrtbTestRecordSearchAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		HolderView holder 	= null ;
		RawSheetIndex item 	= getItem(position);
		
		if(convertView == null){
			holder		= new HolderView() ;
			convertView	= InjectCore.injectOriginalObject(holder);
			convertView.setTag(holder);
		} else {
			holder		= (HolderView)convertView.getTag() ;
		}
		
		holder.recordNo.setText(String.valueOf(item.getID()));
		holder.recordName.setText(CrtbUtils.formatSectionName(item.getPrefix(),(float)item.getFACEDK()));
		
		return convertView ;
	}
	
	@InjectLayout(layout=R.layout.item_search_test_layout)
	class HolderView {
		
		@InjectView(id=R.id.t1)
		TextView recordNo ;
		
		@InjectView(id=R.id.t2)
		TextView recordName ;
	}
}
