package com.crtb.tunnelmonitor.widget;

import java.util.List;

import org.zw.android.framework.impl.ExecuteAsyncTaskImpl;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;

import com.crtb.tunnelmonitor.AppHandler;
import com.crtb.tunnelmonitor.BaseAsyncTask;
import com.crtb.tunnelmonitor.dao.impl.v2.RawSheetIndexDao;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;

/**
 * 测试搜索
 * 
 * @author zhouwei
 *
 */
public final class CrtbTestRecordSearchListView extends CrtbBaseListView {
	
	private CrtbTestRecordSearchAdapter mAdapter ;
	private List<RawSheetIndex> dataList ;
	
	public CrtbTestRecordSearchListView(Context context) {
		this(context, null);
	}

	public CrtbTestRecordSearchListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mAdapter	= new CrtbTestRecordSearchAdapter(context);
		setAdapter(mAdapter);
		
		clearCacheColor() ;
		
		setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				
			}
		}) ;
	}
	
	@Override
	protected AppHandler getAppHandler() {
		return new AppHandler(getContext()){

			@Override
			protected void dispose(Message msg) {
				
				switch(msg.what){
				case 1 :
					
					mAdapter.onClear() ;
					mAdapter.loadEntityDatas(dataList);
					
					break ;
				}
			}
			
		};
	}

	public void search(final String key,final int type){
		
		ExecuteAsyncTaskImpl.defaultSyncExecutor().executeTask(new BaseAsyncTask(mHandler) {
			
			@Override
			public void process() {
				
				RawSheetIndexDao dao = RawSheetIndexDao.defaultDao() ;
				dataList = dao.searchRawSheetIndex(key, type);
				sendMessage(1);
			}
		}) ;
		
	}
	
	public RawSheetIndex getItem(int position){
		return mAdapter.getItem(position);
	}
	
	public void onClear(){
		mAdapter.onClear() ;
	}
	
	@Override
	public void onResume() {
		
	}

	@Override
	public void onReload() {
		
	}
}
