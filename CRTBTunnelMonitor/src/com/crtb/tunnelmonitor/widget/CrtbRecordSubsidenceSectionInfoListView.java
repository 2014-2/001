package com.crtb.tunnelmonitor.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.AppHandler;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceTotalDataDao;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceTotalData;

public class CrtbRecordSubsidenceSectionInfoListView extends CrtbBaseListView {
	
	private String sheetGuid ;
	private CrtbRecordSubsidenceSectionInfoAdapter mAdapter ;
	private List<String> sectionIds = new ArrayList<String>() ;
	
	public CrtbRecordSubsidenceSectionInfoListView(Context context) {
		this(context, null);
	}

	public CrtbRecordSubsidenceSectionInfoListView(final Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mAdapter	= new CrtbRecordSubsidenceSectionInfoAdapter(context);
		setAdapter(mAdapter);
		
		clearCacheColor() ;
		
		final SubsidenceTotalDataDao dao = SubsidenceTotalDataDao.defaultDao() ;
		
		setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				
				if(sheetGuid != null){
					
					SubsidenceCrossSectionIndex section = getItem(position);
					
					 List<SubsidenceTotalData> list = dao.querySubsidenceTotalDatas(sheetGuid, section.getGuid());
					
					if(list != null){
						
						for(SubsidenceTotalData item : list){
							
							if(item.getUploadStatus() == 2){
								Toast.makeText(context, "该断面下的测量数据已经上传，不能取消断面", Toast.LENGTH_SHORT).show();
								return ;
							}
						}
						
					}
				}
				
				// 更新列表
				mAdapter.changeStatus(position);
			}
		}) ;
	}
	
	public SubsidenceCrossSectionIndex getItem(int position){
		return mAdapter.getItem(position);
	}
	
	public String getSelectedSection(){
		return mAdapter.getSelectedSection();
	}
	
	public void setChainage(double value){
		mAdapter.setChainage(value);
	}
	
	// 初始化参数
	public void setSectionIds(String sheetGuid,String section){
		
		this.sheetGuid	= sheetGuid ;
		
		if(section != null){
			
			String[] array = section.split(",");
			
			for(String id: array){
				sectionIds.add(id);
			}
		}
	}

	@Override
	public void onResume() {
		
		if(mAdapter.isEmpty()){
			onReload();
		} else {
			mAdapter.notifyDataSetChanged() ;
		}
	}

	@Override
	protected AppHandler getAppHandler() {
		return new AppHandler(getContext()){

			@SuppressWarnings("unchecked")
			@Override
			protected void dispose(Message msg) {
				
				switch(msg.what){
				case MSG_QUERY_SUBSIDENCE_SECTION_SUCCESS :
					
					List<SubsidenceCrossSectionIndex> list = (ArrayList<SubsidenceCrossSectionIndex>)msg.obj ;
					
					if(list != null && sectionIds != null){
						
						for(SubsidenceCrossSectionIndex item : list){
							
							for(String id : sectionIds){
								
								if(id.equals(String.valueOf(item.getGuid()))){
									item.setUsed(true);
								}
							}
						}
					}
					
					mAdapter.loadEntityDatas(list);
					
					break ;
				}
			}
			
		};
	}

	@Override
	public void onReload() {
		SubsidenceCrossSectionIndexDao.defaultDao().queryAllSection(mHandler);
	}
	
}
