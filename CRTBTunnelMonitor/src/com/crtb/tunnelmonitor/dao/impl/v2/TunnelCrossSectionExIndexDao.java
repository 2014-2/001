package com.crtb.tunnelmonitor.dao.impl.v2;

import org.zw.android.framework.IAccessDatabase;

import com.crtb.tunnelmonitor.entity.TunnelCrossSectionExIndex;

import java.util.List;
import android.util.Log;

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
            obj.setSECT_ID(sectionId);
            insert(obj);
        }
    }

	public TunnelCrossSectionExIndex querySectionById(int rowId) {
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if (mDatabase == null) {
			return null;
		}
		
		String sql = "select * from TunnelCrossSectionExIndex where SECT_ID = ?";
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
	
	public int queryMaxTunnelSectionNo() {

		int maxSectionNo = 0;
		int curNo = -1;
		int secionCharCount = 16;
		String maxSectionNoStr = "";
		String maxSectionCode = "";

		final IAccessDatabase mDatabase = getCurrentDb();

		if (mDatabase == null) {
			return maxSectionNo;
		}

		String sql = "select * from TunnelCrossSectionExIndex";
		List<TunnelCrossSectionExIndex> crosses = mDatabase.queryObjects(sql,
				new String[] {}, TunnelCrossSectionExIndex.class);

		if (crosses == null) {
			return maxSectionNo = 0;
		}
		for (TunnelCrossSectionExIndex cross : crosses) {
			maxSectionCode = cross.getSECTCODE();
			if (maxSectionCode.length() != secionCharCount) {
				Log.d(TAG, "TunnelCrossSectionExIndex data format error");
				maxSectionNo = 0;
				continue;
			} else {
				maxSectionNoStr = maxSectionCode.substring(12);
				try {
					curNo = Integer.valueOf(maxSectionNoStr);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				if (maxSectionNo < curNo) {
					maxSectionNo = curNo;
				}
			}
		}
		return maxSectionNo;
	}
}
