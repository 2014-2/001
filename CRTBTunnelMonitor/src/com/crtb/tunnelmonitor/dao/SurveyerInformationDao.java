package com.crtb.tunnelmonitor.dao;

import java.util.List;

import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionInfo;
import com.crtb.tunnelmonitor.entity.SurveyerInformation;

/**
 *测量人员信息数据库接口
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
