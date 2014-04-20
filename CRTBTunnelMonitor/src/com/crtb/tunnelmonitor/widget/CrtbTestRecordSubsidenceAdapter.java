package com.crtb.tunnelmonitor.widget;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;
import org.zw.android.framework.ioc.InjectView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.dao.impl.v2.RawSheetIndexDao;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

/**
 * 地表下沉断面测量单
 * 
 * @author zhouwei
 *
 */
public class CrtbTestRecordSubsidenceAdapter extends CrtbEntityAdapter<RawSheetIndex> {

	protected CrtbTestRecordSubsidenceAdapter(Context context) {
		super(context);
	}

	protected void changeStatus(int position){
		
		RawSheetIndexDao dao = RawSheetIndexDao.defaultDao() ;
		
		for(int index = 0 ,size = mList.size() ; index < size ; index++){
			
			RawSheetIndex item = mList.get(index) ;
			
			if(index == position){
				item.setChecked(!item.isChecked());
				dao.update(item);
			}
		}
		
		notifyDataSetChanged() ;
	}
	
	protected RawSheetIndex getSelectedSection(){
		
		for(RawSheetIndex item : mList){
			
			if(item.isChecked()){
				return item ;
			}
		}
		
		return null ;
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
		
		if(!item.isChecked()){
			holder.status.setBackgroundResource(R.drawable.no);
		} else {
			holder.status.setBackgroundResource(R.drawable.yes);
		}
		
		return convertView;
	}

	@InjectLayout(layout=R.layout.item_record_section_info_layout)
	class HolderView {
		
		@InjectView(id=R.id.t1)
		TextView recordNo ;
		
		@InjectView(id=R.id.t2)
		TextView recordName ;
		
		@InjectView(id=R.id.t3)
		TextView status ;
	}

}
