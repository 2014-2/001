package com.crtb.tunnelmonitor.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 
 * @author zhouwei
 *
 */
public final class SectionTunnelListView extends CrtbBaseListView {
	
	private SectionTunnelAdapter	mAdapter ;
	
	public SectionTunnelListView(Context context) {
		this(context, null);
	}

	public SectionTunnelListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mAdapter	= new SectionTunnelAdapter(context);
		setAdapter(mAdapter);
		
		clearCacheColor() ;
	}

}
