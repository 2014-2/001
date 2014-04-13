package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionInfo;

/**
 * 
 * @author zhouwei
 * 
 */
public final class SubsidenceCrossSectionDao extends AbstractDao<SubsidenceCrossSectionInfo> {

	private static SubsidenceCrossSectionDao _instance;

	private SubsidenceCrossSectionDao() {

	}

	public static SubsidenceCrossSectionDao defaultDao() {

		if (_instance == null) {
			_instance = new SubsidenceCrossSectionDao();
		}

		return _instance;
	}
	
	public List<SubsidenceCrossSectionInfo> queryAllSection(){
		
		String sql = "select * from SubsidenceCrossSectionInfo" ;
		
		return mDatabase.queryObjects(sql, SubsidenceCrossSectionInfo.class) ;
	}
}
