package com.crtb.tunnelmonitor;

/**
 * App config
 * 
 * @author zhouwei
 *
 */
public final class AppConfig {

	public static boolean DEBUG									= !true ;
	
	public static final int STATUS_NOUSED						= 0 ;
	public static final int STATUS_USED							= 1 ;
	
	// boolean string value
	public static final String BOOLEAN_TRUE						= "true" ;
	public static final String BOOLEAN_FALSE					= "false" ;
	
	// database version
	public static final int DB_VERSION							= 1 ;
	
	// test point type
	public static final String  POINT_A							= "A" ;
	public static final String  POINT_S1_1						= "S1-1" ;
	public static final String  POINT_S1_2						= "S1-2" ;
	public static final String  POINT_S2_1						= "S2-1" ;
	public static final String  POINT_S2_2						= "S2-2" ;
	public static final String  POINT_S3_1						= "S3-1" ;
	public static final String  POINT_S3_2						= "S3-2" ;
	
	// load all surveyer
	public static final String ACTION_RELOAD_ALL_SURVEYER 		= "action.reload.all.surveyer" ;
	
	// export file dir name
	public static final String DB_ROOT							= "/crtb_db/" ;
	public static final String DB_EXPORT_DIR					= "/crtb_export/" ;
	public static final String DB_IMPORT_DIR					= "/crtb_import/" ;
}
