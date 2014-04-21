package com.crtb.tunnelmonitor;

import org.zw.android.framework.IExecuteAsyncTask.IAsyncTask;
import org.zw.android.framework.impl.Worker;

/**
 * 
 * @author zhouwei
 *
 */
public abstract class BaseSyncTask extends IAsyncTask implements MessageDefine {

	protected static boolean debug 		= false ;
	protected AppHandler 			mBaseHandler ;
	
	public BaseSyncTask(){
		this(null); ;
	}
	
	public BaseSyncTask(AppHandler handler){
		mBaseHandler	= handler ;
	}
	
	public final void setWorker(Worker worker){
		
		if(mBaseHandler != null){
			mBaseHandler.setWorker(worker);
		}
	}
	
	@Override
	public final Object onProcessing() {
		
		// debug
		if(debug){
			System.out.println("zhouwei: BaseSyncTask: ++++++++++++++++Start Task+++++++++");
		}
		
		sendMessage(MSG_TASK_START);
		
		try{
			// process
			process() ;
		} catch(Exception e){
			e.printStackTrace() ;
		} finally{
			sendMessage(MSG_TASK_END);
		}
		
		// debug
		if(debug){
			System.out.println("zhouwei: BaseSyncTask: --------------End Task----------");
		}
		
		return null;
	}
	
	public final void sendMessage(int what){
		if(mBaseHandler != null){
			mBaseHandler.obtainMessage(what).sendToTarget() ;
		}
	}
	
	public final void sendMessage(int what,Object obj){
		if(mBaseHandler != null){
			mBaseHandler.obtainMessage(what, obj).sendToTarget() ;
		}
	}
	
	/** task process */
	public abstract void process() ;
	
}
