package com.crtb.tunnelmonitor.dao;

import java.util.List;

import com.crtb.tunnelmonitor.entity.SubsidenceTotalDataInfo;

/**
 * 断面测量记录单数据库接口
 */
public interface SubsidenceTotalDataDao {
	/*条件查询记录单*/
	public List<SubsidenceTotalDataInfo> GetAllSubsidenceTotalData(int stationId,int sectionId,int rawsheetId,int type);
	
	/* 添加测量记录单 */
	public Boolean InsertSubsidenceTotalData(SubsidenceTotalDataInfo s);
	
	/*修改测量记录单*/
	public Boolean UpdateSubsidenceTotalData(SubsidenceTotalDataInfo s);
	
	/* 删除测量记录单 */
	public Boolean DeleteSubsidenceTotalData(int id,int level,int type);
	
	/* 根据id获取单条测量记录 */
	public SubsidenceTotalDataInfo GetSubsidenceTotalData(int id,int type);
}
