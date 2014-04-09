package com.crtb.tunnelmonitor.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

/**
 * 
 * @author zhouwei
 *
 * @param <T>
 */
public abstract class CrtbEntityAdapter<T> extends CrtbBaseAdapter {
	
	private final List<T> mList	= new ArrayList<T>();

	protected CrtbEntityAdapter(Context context) {
		super(context);
		
		mList.clear() ;
	}

	@Override
	public int getCount() {
		return mList.size() ;
	}

	@Override
	public T getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position ;
	}
	
	public void loadEntityDatas(List<T> list){
	
		if(list == null){
			return ;
		}
		
		List<T> temp = mList ;
		temp.clear() ;
		
		for(T t : list){
			temp.add(t);
		}
		
		notifyDataSetChanged() ;
	}
	
	protected void onClear(){
		super.onClear() ;
		
		mList.clear() ;
		notifyDataSetChanged() ;
	}

}
