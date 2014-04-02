package com.sxlc.dao;

import java.util.List;

import com.sxlc.entity.TotalStationInfo;
import com.sxlc.entity.WorkInfos;

/**
 * 全站仪连接参数信息数据库接口
 *创建时间：2014-3-24下午13:50:00
 *@author 张友
 *@since JDK1.6
 *@version 1.0
 */
public interface TotalStationDao {
	/**查询全部全站仪连接参数信息*/
	public List<TotalStationInfo> SelectAllTotalStation();
	/**新建全站仪连接参数信息*/
	public Boolean InsertTotalStation(TotalStationInfo s);
	/**查询全站仪连接参数信息*/
	public TotalStationInfo SelectTotalStation(int id);
	/**删除全站仪连接参数信息*/
	public Boolean DeleteTotalStation(int id);
	/**编辑全站仪连接参数信息*/
	public Boolean UpdateTotalStation(TotalStationInfo s);
	void GetTotalStationList(List<TotalStationInfo> list);
}
