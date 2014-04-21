package com.crtb.tunnelmonitor;

public interface MessageDefine {

	public static final int MSG_TASK_START						= 0x00f00001 ;
	public static final int MSG_TASK_END						= MSG_TASK_START + 1 ;
	
	// 查询断面信息
	public static final int MSG_QUERY_SECTION_SUCCESS			= MSG_TASK_END + 1 ;
}
