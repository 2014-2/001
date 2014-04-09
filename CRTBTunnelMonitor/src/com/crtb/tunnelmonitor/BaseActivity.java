package com.crtb.tunnelmonitor;

import org.zw.android.framework.IFrameworkFacade;

import com.crtb.tunnelmonitor.activity.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


/**
 * Base Activity
 * 
 * @author zhouwei
 *
 */
public abstract class BaseActivity extends Activity {

	// base handler
	protected AppHandler 			mHanlder ;
	
	// Framework
	protected IFrameworkFacade 		mFramework ;
	
	// topbar title
	private TextView				mTopbarTitle ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mFramework	= ((AppCRTBApplication)getApplication()).getFrameworkFacade();
		mHanlder 	= getHandler() ;
	}
	
	protected AppHandler getHandler(){
		return new AppHandler(this) ;
	}
	
	/**
	 * set topbar title
	 * 
	 * @param title
	 */
	protected void setTopbarTitle(String title){
		
		if(mTopbarTitle == null){
			mTopbarTitle = (TextView)findViewById(R.id.tv_topbar_title) ;
		}
		
		mTopbarTitle.setText(title);
	}
}
