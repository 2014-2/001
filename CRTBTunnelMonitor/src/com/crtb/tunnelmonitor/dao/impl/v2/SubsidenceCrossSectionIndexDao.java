package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import org.zw.android.framework.IAccessDatabase;

import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;

/**
 * 地表下沉断面DAO
 * 
 * @author zhouwei
 * 
 */
public final class SubsidenceCrossSectionIndexDao extends AbstractDao<SubsidenceCrossSectionIndex> {

	private static SubsidenceCrossSectionIndexDao _instance;

	private SubsidenceCrossSectionIndexDao() {

	}

	public static SubsidenceCrossSectionIndexDao defaultDao() {

		if (_instance == null) {
			_instance = new SubsidenceCrossSectionIndexDao();
		}

		return _instance;
	}
	
	public List<SubsidenceCrossSectionIndex> queryAllSection(){
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return null ;
		}
		
		String sql = "select * from SubsidenceCrossSectionIndex" ;
		
		return mDatabase.queryObjects(sql, SubsidenceCrossSectionIndex.class) ;
	}
}
