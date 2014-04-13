package com.crtb.tunnelmonitor.dao.impl.v2;

import com.crtb.tunnelmonitor.entity.RecordSubsidenceInfo;

/**
 * 
 * @author zhouwei
 * 
 */
public final class SubsidenceCrossSectionDao extends AbstractDao<RecordSubsidenceInfo> {

	private static SubsidenceCrossSectionDao _instance;

	private SubsidenceCrossSectionDao() {

	}

	public static SubsidenceCrossSectionDao defaultDao() {

		if (_instance == null) {
			_instance = new SubsidenceCrossSectionDao();
		}

		return _instance;
	}
}
