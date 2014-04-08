package com.crtb.tunnelmonitor.activity;

import org.zw.android.framework.IFrameworkFacade;

import android.app.Activity;
import android.os.Bundle;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.AppHandler;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mFramework	= ((AppCRTBApplication)getApplication()).getFrameworkFacade();
		mHanlder 	= getHandler() ;
	}
	
	protected AppHandler getHandler(){
		return new AppHandler(this) ;
	}
}
