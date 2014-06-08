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
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelSettlementTotalDataDao;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;

/**
 * 
 * @author zhouwei
 *
 */
public class CrtbRecordTunnelSectionInfoListView extends CrtbBaseListView {
	
	private String sheetGuid ;
	private CrtbRecordTunnelSectionInfoAdapter mAdapter ;
	private List<String> sectionIds = new ArrayList<String>() ;
	
	public CrtbRecordTunnelSectionInfoListView(Context context) {
		this(context, null);
	}

	public CrtbRecordTunnelSectionInfoListView(final Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mAdapter	= new CrtbRecordTunnelSectionInfoAdapter(context);
		setAdapter(mAdapter);
		
		clearCacheColor() ;
		
		final TunnelSettlementTotalDataDao dao = TunnelSettlementTotalDataDao.defaultDao() ;
		
		setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				
				if(sheetGuid != null){
					
					TunnelCrossSectionIndex section = mAdapter.getItem(position);
					
					// 改断面下的所有测量数据，上传状态
					List<TunnelSettlementTotalData> list = dao.queryTunnelTotalDatas(sheetGuid, section.getGuid());
			
					if(list != null){
						
						for(TunnelSettlementTotalData item : list){
							
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
	
	public void setChainage(double value){
		mAdapter.setChainage(value);
	}
	
	public TunnelCrossSectionIndex getItem(int position){
		return mAdapter.getItem(position);
	}
	
	public String getSelectedSection(){
		return mAdapter.getSelectedSection();
	}
	
	/**
	 * 设置初始值
	 * 
	 * @param sheetGuid		: 记录单guid
	 * @param section
	 */
	public void setSectionIds(String sheetGuid,String section){
		
		this.sheetGuid = sheetGuid ;
		
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
				
				if(msg.what == MSG_QUERY_SECTION_SUCCESS){
					
					List<TunnelCrossSectionIndex> list = (ArrayList<TunnelCrossSectionIndex>)msg.obj ;
					
					if(list != null && sectionIds != null){
						
						for(TunnelCrossSectionIndex item : list){
							
							for(String id : sectionIds){
								if(id.equals(String.valueOf(item.getGuid()))){
									item.setUsed(true);
									break ;
								}
							}
						}
						
					}
					
					mAdapter.loadEntityDatas(list);
				}
			}
			
		};
	}

	@Override
	public void onReload() {
		TunnelCrossSectionIndexDao.defaultDao().queryAllSection(mHandler) ;
	}
	
}
