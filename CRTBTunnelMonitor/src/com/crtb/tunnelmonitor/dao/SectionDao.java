package com.crtb.tunnelmonitor.dao;

import java.util.List;

import com.crtb.tunnelmonitor.entity.SectionInfo;
/**
 * 断面接口
 */
public interface SectionDao {

	/**查询全部*/
	public List<SectionInfo> SectionAll();
	/**新建记录单*/
	public Boolean InsertSection(SectionInfo s);
	/**查询记录单*/
	public SectionInfo SelectSection();
	/**删除记录单*/
	public void DeleteSection(Double d);
	/**编辑记录单*/
	public void UpdateSection(SectionInfo s);
	
	
	/**查询全部*/
	public List<SectionInfo> SectiondibiaoAll();
	/**新建记录单*/
	public Boolean InsertSectiondibiao(SectionInfo s);
	/**查询记录单*/
	public SectionInfo SelectSectiondibiao(SectionInfo s);
	/**删除记录单*/
	public void DeleteSectiondibiao(Double d);
	/**编辑记录单*/
	public void UpdateSectiondibiao(SectionInfo s);
	
	
}
