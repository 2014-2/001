package com.crtb.tunnelmonitor.dao;

import java.util.List;

import com.crtb.tunnelmonitor.entity.TunnelCrossSectionInfo;
/**
 * 断面接口
 */
public interface TunnelCrossSectionDao {
	/**查询全部*/
	public List<TunnelCrossSectionInfo> SectionAll();
	/**新建记录单*/
	public Boolean InsertSection(TunnelCrossSectionInfo s);
	/**查询记录单*/
	public TunnelCrossSectionInfo SelectSection();
	/**删除记录单*/
	public int DeleteSection(int id);
	/**编辑记录单*/
	public void UpdateSection(TunnelCrossSectionInfo s);
	
	
	/**查询全部*/
	public List<TunnelCrossSectionInfo> SectiondibiaoAll();
	/**新建记录单*/
	public Boolean InsertSectiondibiao(TunnelCrossSectionInfo s);
	/**查询记录单*/
	public TunnelCrossSectionInfo SelectSectiondibiao(TunnelCrossSectionInfo s);
	/**删除记录单*/
	public void DeleteSectiondibiao(Double d);
	/**编辑记录单*/
	public void UpdateSectiondibiao(TunnelCrossSectionInfo s);
	void GetTunnelCrossSectionList(List<TunnelCrossSectionInfo> lt);
}
