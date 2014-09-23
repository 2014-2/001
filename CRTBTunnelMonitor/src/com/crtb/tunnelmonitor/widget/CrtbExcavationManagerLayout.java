package com.crtb.tunnelmonitor.widget;

import java.util.List;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;
import org.zw.android.framework.ioc.InjectView;

import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.dao.impl.v2.ExcavateMethodDao;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionParameter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

@InjectLayout(layout=R.layout.custom_excavation_manager_layout)
public class CrtbExcavationManagerLayout extends LinearLayout {
	
	private ExcavateMethodDao dao ;
	private ExcavationClick onClick ;
	
	public CrtbExcavationManagerLayout(Context context) {
		this(context, null);
	}

	public CrtbExcavationManagerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setOrientation(VERTICAL);
		
		InjectCore.injectUIProperty(this);
		
		dao	= ExcavateMethodDao.defaultDao() ;
		
		onResume() ;
	}
	
	public void setExcavationClick(ExcavationClick click){
		onClick	= click ;
	}

	public void onResume(){
		onReload();
	}
	
	public void removeExcavation(TunnelCrossSectionParameter item){
		
		if(item == null) return ;
		
		if(dao.deleteExcavateMethod(item.getGuid())){
			onReload() ;
		}
	}
	
	private void onReload(){
		
		removeAllViews() ;
		
		List<TunnelCrossSectionParameter> list = dao.queryNoBaseCustomExcavateMethod() ;
		
		if(list == null || list.isEmpty())
			return ;
		
		for(final TunnelCrossSectionParameter item : list){
			
			HolderView holder = new HolderView() ;
			View view = InjectCore.injectOriginalObject(holder);
			
			holder.name.setText(item.getMethodName());
			
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					if(onClick != null) onClick.onClick(item);
				}
			}) ;
			
			addView(view);
		}
	}
	
	@InjectLayout(layout=R.layout.item_excavation_layout)
	class HolderView {
		
		@InjectView(id=R.id.t1)
		TextView name ;
	}
	
	public static interface ExcavationClick {
		
		public void onClick(TunnelCrossSectionParameter bean) ;
	}
	
}
