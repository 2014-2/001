package com.crtb.tunnelmonitor;

/**
 * App config
 * 
 * @author zhouwei
 *
 */
public final class AppConfig {

	public static boolean DEBUG									= true ;
	
	public static final int STATUS_NOUSED						= 0 ;
	public static final int STATUS_USED							= 1 ;
	
	// boolean string value
	public static final String BOOLEAN_TRUE						= "true" ;
	public static final String BOOLEAN_FALSE					= "false" ;
	
	// database version
	public static final int DB_VERSION							= 1 ;
	
	// load all surveyer
	public static final String ACTION_RELOAD_ALL_SURVEYER 		= "action.reload.all.surveyer" ;
}
