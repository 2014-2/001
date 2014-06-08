package com.crtb.tunnelmonitor.widget;

import java.util.ArrayList;
import java.util.List;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;
import org.zw.android.framework.ioc.InjectView;
import org.zw.android.framework.util.DateUtils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.dao.impl.v2.RawSheetIndexDao;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;

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

	public void changeStatus(RawSheetIndex obj){
		
		if(obj == null){
			return ;
		}
		
		int position = mList.indexOf(obj);
		
		if(position < 0){
			return ;
		}
		
		RawSheetIndexDao dao = RawSheetIndexDao.defaultDao() ;
		
		for(int index = 0 ,size = mList.size() ; index < size ; index++){
			
			RawSheetIndex item = mList.get(index) ;
			
			if(index == position){
				item.setChecked(true);
			} else {
				item.setChecked(false);
			}
			
			dao.update(item);
		}
		
		notifyDataSetChanged() ;
	}
	
	protected List<RawSheetIndex> getSelectedSection(){
		
		List<RawSheetIndex> list = new ArrayList<RawSheetIndex>();
		
		for(RawSheetIndex item : mList){
			
			if(item.isChecked()){
				list.add(item) ;
			}
		}
		
		return list ;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		HolderView holder 	= null ;
		RawSheetIndex item 	= getItem(position);
		
		if(convertView == null){
			holder		= new HolderView() ;
			convertView	= InjectCore.injectOriginalObject(holder);
			convertView.setTag(holder);
		} else {
			holder		= (HolderView)convertView.getTag() ;
		}
		
		holder.recordNo.setText(String.valueOf(position + 1));
		holder.recordName.setText(DateUtils.toDateString(item.getCreateTime(),DateUtils.DATE_TIME_FORMAT));
		
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
