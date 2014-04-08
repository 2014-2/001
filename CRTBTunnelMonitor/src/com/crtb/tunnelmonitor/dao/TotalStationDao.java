package com.crtb.tunnelmonitor.dao;

import java.util.List;

import com.crtb.tunnelmonitor.entity.TotalStationInfo;
import com.crtb.tunnelmonitor.entity.WorkInfos;

/**
 * 全站仪连接参数信息数据库接口
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
