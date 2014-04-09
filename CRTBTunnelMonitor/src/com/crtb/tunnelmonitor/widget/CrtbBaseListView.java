package com.crtb.tunnelmonitor.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.crtb.tunnelmonitor.AppHandler;

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
		init() ;
	}

	public CrtbBaseListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs,defStyle);
		init() ;
	}
	
	private void init(){
		mHandler	= getAppHandler() ;
	}

	protected AppHandler getAppHandler(){
		return new AppHandler(getContext());
	}
}
