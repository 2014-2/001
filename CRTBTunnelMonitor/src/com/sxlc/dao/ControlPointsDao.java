package com.sxlc.dao;

import java.util.List;

import com.sxlc.entity.ControlPointsInfo;

/**
 * 控制点数据库接口
 *创建时间：2014-3-24下午13:50:00
 *@author 张友
 *@since JDK1.6
 *@version 1.0
 */
public interface ControlPointsDao {
	/* 查看所有设站信息 */
	public List<ControlPointsInfo> GetAllStation();
	
	/* 添加设站信息 */
	public boolean InsertStationInfo(ControlPointsInfo s);
	
	/* 修改设站信息 */
	public boolean UpdateStationInfo(ControlPointsInfo s);
	
	/* 获取单个设站信息 */
	public ControlPointsInfo GetControlPoints(int id);
	
	/* 删除设站信息 */
	public boolean DeleteStationInfo(int id);

	void GetControlPointsList(List<ControlPointsInfo> list);
}
