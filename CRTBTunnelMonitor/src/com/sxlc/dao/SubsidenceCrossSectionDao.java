package com.sxlc.dao;
import java.util.List;

import com.sxlc.entity.SubsidenceCrossSectionInfo;

/**
 * 地表下沉断面Dao
 *创建时间：2014-3-24上午11:50:00
 *@author 张友
 *@since JDK1.6
 *@version 1.0
 */
public interface SubsidenceCrossSectionDao {
	/**查询全部*/
	public List<SubsidenceCrossSectionInfo> SelectAllSection();
	/**新建记录单*/
	public Boolean InsertSubsidenceCrossSection(SubsidenceCrossSectionInfo s);
	/**查询记录单*/
	public SubsidenceCrossSectionInfo SelectSubsidenceCrossSection(int id);
	/**删除记录单*/
	public int DeleteSubsidenceCrossSection(int id);
	/**编辑记录单*/
	public Boolean UpdateSubsidenceCrossSection(SubsidenceCrossSectionInfo s);
	public void GetSubsidenceCrossSectionList(List<SubsidenceCrossSectionInfo> lt);
}
