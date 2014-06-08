package com.crtb.tunnelmonitor;

import org.zw.android.framework.impl.Worker;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * App Handler
 * 
 * @author zhouwei
 *
 */
public class AppHandler extends Handler implements MessageDefine {
	
	static String TAG 		= "AppHandler" ;
	
	protected Context 		mOwner ;
	private Worker 			mWorker ;

	public AppHandler(){
		this(null);
	}
	
	public AppHandler(Context owner){
		mOwner	= owner ;
	}
	
	public Worker getWorker() {
		return mWorker;
	}

	public void setWorker(Worker mWorker) {
		this.mWorker = mWorker;
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
		
		// do some common something ; eg : connection dialog
		switch(msg.what){
		case MessageDefine.MSG_TASK_START :
			
			break ;
		case MessageDefine.MSG_TASK_END :
			
			break ;
		}
		
		// dispatch message
		dispose(msg);
	}
	
	// please override the method
	protected void dispose(Message msg){
		
	}
	
}
