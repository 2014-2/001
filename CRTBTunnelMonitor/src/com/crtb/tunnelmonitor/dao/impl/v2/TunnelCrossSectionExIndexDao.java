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

    public void insertIfNotExist(int sectionId, String sectionCode) {
        if (queryOneBySectionCode(sectionCode) == null) {
            TunnelCrossSectionExIndex obj = new TunnelCrossSectionExIndex();
            obj.setSECTCODE(sectionCode);
            obj.setID(sectionId);
            insert(obj);
        }
    }

	public TunnelCrossSectionExIndex querySectionById(int rowId) {
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if (mDatabase == null) {
			return null;
		}
		
		String sql = "select * from TunnelCrossSectionExIndex where ID = ?";
		return mDatabase.queryObject(sql, new String[] { String.valueOf(rowId) }, TunnelCrossSectionExIndex.class);
	}

	public TunnelCrossSectionExIndex queryOneBySectionCode(String sectionCode) {
	    
	    final IAccessDatabase mDatabase = getCurrentDb();
	    
	    if (mDatabase == null) {
	        return null;
	    }
	    
	    String sql = "select * from TunnelCrossSectionExIndex where SECTCODE = ?";
	    return mDatabase.queryObject(sql, new String[] { sectionCode }, TunnelCrossSectionExIndex.class);
	}
	
}
