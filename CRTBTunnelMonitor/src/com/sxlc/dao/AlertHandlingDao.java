package com.sxlc.dao;

import java.util.List;

import com.sxlc.entity.AlertHandlingInfo;


/**
 * 预警日志数据库接口
 *创建时间：2014-3-24下午13:50:00
 *@author 张友
 *@since JDK1.6
 *@version 1.0
 */
public interface AlertHandlingDao {
	/**查询全部*/
	public List<AlertHandlingInfo> SelectAllAlertHandling();
	/**新建预警*/
	public Boolean InsertAlertHandling(AlertHandlingInfo s);
}
