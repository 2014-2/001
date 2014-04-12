package com.crtb.tunnelmonitor.widget;

import android.content.Context;
import android.util.AttributeSet;

public class SectionSubsidenceListView extends CrtbBaseListView {
	
	private SectionSubsidenceAdapter 	mAdapter ;

	public SectionSubsidenceListView(Context context) {
		this(context, null);
	}
	
	public SectionSubsidenceListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mAdapter	= new SectionSubsidenceAdapter(context);
		setAdapter(mAdapter);
		
		clearCacheColor() ;
	}

}
