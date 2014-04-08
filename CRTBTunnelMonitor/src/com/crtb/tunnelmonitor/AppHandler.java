package com.crtb.tunnelmonitor;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * App Handler
 * 
 * @author zhouwei
 *
 */
public abstract class AppHandler extends Handler {
	
	static String TAG 		= "AppHandler" ;
	
	protected Context 		mOwner ;

	public AppHandler(){
		this(null);
	}
	
	public AppHandler(Context owner){
		mOwner	= owner ;
	}
	
	public void sendMessage(int what){
		obtainMessage(what).sendToTarget() ;
	}
	
	public void sendMessage(int what,Object obj){
		obtainMessage(what,obj).sendToTarget() ;
	}

	@Override
	public final void handleMessage(Message msg) {
		
		if(AppConfig.DEBUG){
			AppLogger.d(TAG, " >> handleMessage" + msg.what);
		}
		
		// do some common something
		switch(msg.what){
			
		}
		
		// dispatch message
		dispose(msg);
	}
	
	protected abstract void dispose(Message msg) ;
	
}
