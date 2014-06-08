package com.crtb.tunnelmonitor;

import android.util.Log;

/**
 * Logger
 * 
 * @author zhouwei
 *
 */
public final class AppLogger {

	private AppLogger(){
		
	}
	
	public static void d(String tag,String message){
		
		if(AppConfig.DEBUG){
			Log.d(tag, message);
		}
	}
	
	public static void w(String tag,String message){
		
		if(AppConfig.DEBUG){
			Log.w(tag, message);
		}
	}
	
	public static void e(String tag,String message){
		
		if(AppConfig.DEBUG){
			Log.e(tag, message);
		}
	}
}
