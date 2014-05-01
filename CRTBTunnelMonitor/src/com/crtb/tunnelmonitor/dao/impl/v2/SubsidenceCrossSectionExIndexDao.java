package com.crtb.tunnelmonitor.dao.impl.v2;

import org.zw.android.framework.IAccessDatabase;

import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionExIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionExIndex;

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

    public void insertIfNotExist(int sectionId, String sectionCode) {
        if (queryOneBySectionCode(sectionCode) == null) {
            SubsidenceCrossSectionExIndex obj = new SubsidenceCrossSectionExIndex();
            obj.setSECTCODE(sectionCode);
            obj.setID(sectionId);
            insert(obj);
        }
    }

	public SubsidenceCrossSectionExIndex querySectionById(int rowId) {
		final IAccessDatabase mDatabase = getCurrentDb();
		if (mDatabase == null) {
			return null;
		}
		String sql = "select * from TunnelCrossSectionExIndex where ID = ?";
		return mDatabase.queryObject(sql, new String[] { String.valueOf(rowId) }, SubsidenceCrossSectionExIndex.class);
	}

    public SubsidenceCrossSectionExIndex queryOneBySectionCode(String sectionCode) {
        
        final IAccessDatabase mDatabase = getCurrentDb();
        
        if (mDatabase == null) {
            return null;
        }
        
        String sql = "select * from TunnelCrossSectionExIndex where SECTCODE = ?";
        return mDatabase.queryObject(sql, new String[] { sectionCode }, SubsidenceCrossSectionExIndex.class);
    }
}
