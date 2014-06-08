package com.crtb.tunnelmonitor.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

/**
 * Base Adapter
 * 
 * @author zhouwei
 *
 */
public abstract class CrtbBaseAdapter extends BaseAdapter {

	protected Context 			mContext ;
	protected LayoutInflater 	mInflater ;
	
	protected CrtbBaseAdapter(Context context){
		mContext	= context ;
		mInflater	= LayoutInflater.from(mContext);
	}
	
	protected void onClear(){
		
	}
}
