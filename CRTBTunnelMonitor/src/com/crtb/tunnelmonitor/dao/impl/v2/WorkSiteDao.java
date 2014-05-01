package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import org.zw.android.framework.IAccessDatabase;

import com.crtb.tunnelmonitor.entity.WorkSite;

public class WorkSiteDao extends AbstractDao<WorkSite> {
	private static WorkSiteDao _instance;

	private WorkSiteDao() {

	}
	
	public static WorkSiteDao defaultDao() {
		if (_instance == null) {
			_instance = new WorkSiteDao();
		}
		return _instance;
	}

	public List<WorkSite> queryAllWorkSite() {
		final IAccessDatabase mDatabase = getCurrentDb();
		if (mDatabase == null) {
			return null;
		}
		String sql = "select * from WorkSite order by ID ASC";
		return mDatabase.queryObjects(sql, WorkSite.class);
	}
	
}
