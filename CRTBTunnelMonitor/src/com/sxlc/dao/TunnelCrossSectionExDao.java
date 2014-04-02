package com.sxlc.dao;

import java.util.List;

import com.sxlc.entity.TunnelCrossSectionExInfo;

/**
 * 断面测量数据库接口
 *创建时间：2014-3-24下午13:50:00
 *@author 张友
 *@since JDK1.6
 *@version 1.0
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
