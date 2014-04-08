package com.crtb.tunnelmonitor.dao;

import java.util.List;

import com.crtb.tunnelmonitor.entity.AlertHandlingInfo;


/**
 * 预警日志数据库接口
 */
public interface AlertHandlingDao {
	/**查询全部*/
	public List<AlertHandlingInfo> SelectAllAlertHandling();
	/**新建预警*/
	public Boolean InsertAlertHandling(AlertHandlingInfo s);
}
