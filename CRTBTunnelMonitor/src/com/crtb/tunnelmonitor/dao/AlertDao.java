package com.crtb.tunnelmonitor.dao;

import java.util.List;

import com.crtb.tunnelmonitor.entity.AlertInfo;

/**
 * 预警内容数据库接口
 *创建时间：2014-3-24下午13:50:00
 *@author 张友
 *@since JDK1.6
 *@version 1.0
 */
public interface AlertDao {
	/**查询全部*/
	public List<AlertInfo> SelectAllAlert();
	/**新建预警*/
	public Boolean InsertAlert(AlertInfo s);
	/**查询预警*/
	public AlertInfo SelectAlert(int id);
	/**删除预警*/
	public Boolean DeleteAlert(int id);
	/**编辑预警*/
	public Boolean UpdateAlert(AlertInfo s);
}
