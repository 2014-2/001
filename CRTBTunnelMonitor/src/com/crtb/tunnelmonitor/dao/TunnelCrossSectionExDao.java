package com.crtb.tunnelmonitor.dao;

import java.util.List;

import com.crtb.tunnelmonitor.entity.TunnelCrossSectionExInfo;

/**
 * 断面测量数据库接口
 */

public interface TunnelCrossSectionExDao {
	/* 查看所有  */
	public List<TunnelCrossSectionExInfo> GetAllTunnelCrossSection();
	
	/* 增加测量记录 */
	public Boolean InsertTunnelCrossSection(TunnelCrossSectionExInfo t);
	
	/* 修改测量记录 */
	public Boolean UpdateTunnelCrossSection(TunnelCrossSectionExInfo t);
	
	/* 删除测量记录 */
	public Boolean DeleteTunnelCrossSection(int id);
	
	/* 查看单条记录 */
	public TunnelCrossSectionExInfo GetTunnelCrossSectionExInfo(int id);
}
