package com.crtb.tunnelmonitor;

import org.zw.android.framework.IFrameworkFacade;

import com.crtb.tunnelmonitor.activity.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;
import android.widget.Toast;


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
	
	//
	protected DisplayMetrics 		mDisplayMetrics ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mFramework	= ((AppCRTBApplication)getApplication()).getFrameworkFacade();
		mHanlder 	= getHandler() ;
		
		mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
	}
	
	protected AppHandler getHandler(){
		return new AppHandler(this) ;
	}
	
	protected void showText(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show() ;
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
