package com.crtb.tunnelmonitor.dao.impl.v2;

import org.zw.android.framework.IAccessDatabase;

import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionExIndex;

public class SubsidenceCrossSectionExIndexDao  extends AbstractDao<SubsidenceCrossSectionExIndex> {
	private static SubsidenceCrossSectionExIndexDao _instance;

	private SubsidenceCrossSectionExIndexDao() {

	}

	public static SubsidenceCrossSectionExIndexDao defaultDao() {

		if (_instance == null) {
			_instance = new SubsidenceCrossSectionExIndexDao();
		}

		return _instance;
	}
	
	public SubsidenceCrossSectionExIndex querySectionById(int rowId) {
		final IAccessDatabase mDatabase = getCurrentDb();
		if (mDatabase == null) {
			return null;
		}
		String sql = "select * from TunnelCrossSectionExIndex where ID = ?";
		return mDatabase.queryObject(sql, new String[] { String.valueOf(rowId) }, SubsidenceCrossSectionExIndex.class);
	}

}
