package com.crtb.tunnelmonitor.dao.impl.v2;

import org.zw.android.framework.IAccessDatabase;

import com.crtb.tunnelmonitor.entity.TunnelCrossSectionExIndex;

public class TunnelCrossSectionExIndexDao extends AbstractDao<TunnelCrossSectionExIndex> {

	private static TunnelCrossSectionExIndexDao _instance;

	private TunnelCrossSectionExIndexDao() {

	}

	public static TunnelCrossSectionExIndexDao defaultDao() {

		if (_instance == null) {
			_instance = new TunnelCrossSectionExIndexDao();
		}

		return _instance;
	}
	
	public TunnelCrossSectionExIndex querySectionById(int rowId) {
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if (mDatabase == null) {
			return null;
		}
		
		String sql = "select * from TunnelCrossSectionExIndex where ID = ?";
		return mDatabase.queryObject(sql, new String[] { String.valueOf(rowId) }, TunnelCrossSectionExIndex.class);
	}
	
}
