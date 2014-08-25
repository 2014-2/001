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
	public static final String DB_SUFFIX						= ".db" ;
	public static final String DB_SUFFIX_BACKUP					= ".dbbp" ;// 数据库备份文件
	public static final String DB_TEMP_SUFFIX					= ".bin" ;
	
	public static String getTSErrorCode(int code){
		
		if(code == 1285){
			return "EDM设置错误，检查EDM模式及目标设置" ;
		} else if(code == 1283){
			return "警告：此次测量操作只有角度观测值有效！" ;
		} else if(code == 786){
			return "系统忙或正在使用，不能执行该功能" ;
		} else if(code == 770){
			return "无效指令" ;
		} else if(code == 111111){
			return "全站仪已经连接" ;
		} else if(code == 0){
			return "未知错误" ;
		} else if(code == -1){
			return "仪器返回的未知数据格式" ;
		} else if(code == -2){
			return "仪器无回应" ;
		} else if(code == -3){
			return "全站仪连接失败" ;
		}
		
		return "" ;
	}
}
