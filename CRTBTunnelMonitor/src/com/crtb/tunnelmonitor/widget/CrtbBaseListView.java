package com.crtb.tunnelmonitor.widget;

import com.crtb.tunnelmonitor.AppHandler;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Base ListView
 * 
 * @author zhouwei
 *
 */
public abstract class CrtbBaseListView extends ListView {
	
	protected AppHandler 		mHandler ;

	public CrtbBaseListView(Context context) {
		this(context, null);
	}

	public CrtbBaseListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mHandler	= getAppHandler() ;
	}

	public CrtbBaseListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs,defStyle);
		
		mHandler	= getAppHandler() ;
	}

	protected AppHandler getAppHandler(){
		return new AppHandler(getContext());
	}
}
