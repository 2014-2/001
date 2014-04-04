package com.sxlc.dao;

import java.util.List;

import com.sxlc.entity.SubsidenceCrossSectionInfo;
import com.sxlc.entity.SurveyerInformation;

/**
 *测量人员信息数据库接口
 *创建时间：2014-3-24下午13:50:00
 *@author 张友
 *@since JDK1.6
 *@version 1.0
 */
public interface SurveyerInformationDao {
	/**查询全部*/
	public List<SurveyerInformation> SelectAllSurveyerInfo();
	/**新建测量人员*/
	public Boolean InsertSurveyerInfo(SurveyerInformation s);
	/**查询测量人员*/
	public SurveyerInformation SelectSurveyerInfo(int id);
	/**删除测量人员*/
	public Boolean DeleteSurveyerInfo(int id);
	/**编辑测量人员*/
	public Boolean UpdateSurveyerInfo(SurveyerInformation s);
}
