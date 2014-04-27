package com.crtb.tunnelmonitor;

public interface MessageDefine {

	public static final int MSG_TASK_START								= 0x00f00001 ;
	public static final int MSG_TASK_END								= MSG_TASK_START + 1 ;
	
	// 查询断面信息
	public static final int MSG_QUERY_SECTION_SUCCESS					= MSG_TASK_END + 1 ;
	
	// 查询下沉断面信息
	public static final int MSG_QUERY_SUBSIDENCE_SECTION_FAILED			= MSG_QUERY_SECTION_SUCCESS + 1 ;
	public static final int MSG_QUERY_SUBSIDENCE_SECTION_SUCCESS		= MSG_QUERY_SUBSIDENCE_SECTION_FAILED + 1 ;
	
	// 导出数据库文件
	public static final int MSG_EXPORT_DB_FAILED						= MSG_QUERY_SUBSIDENCE_SECTION_SUCCESS + 1 ;
	public static final int MSG_EXPORT_DB_SUCCESS						= MSG_EXPORT_DB_FAILED + 1 ;
	
	// 导入数据库文件
	public static final int MSG_INPORT_DB_FAILED						= MSG_EXPORT_DB_SUCCESS + 1 ;
	public static final int MSG_INPORT_DB_SUCCESS						= MSG_INPORT_DB_FAILED + 1 ;
}
