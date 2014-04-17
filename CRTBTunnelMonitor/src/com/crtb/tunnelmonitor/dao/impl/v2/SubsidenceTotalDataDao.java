package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import com.crtb.tunnelmonitor.entity.SubsidenceTotalData;

public class SubsidenceTotalDataDao extends AbstractDao<SubsidenceTotalData> {

	private static SubsidenceTotalDataDao _instance ;
	
	private SubsidenceTotalDataDao(){
		
	}
	
	public static SubsidenceTotalDataDao defaultDao(){
		
		if(_instance == null){
			_instance	= new SubsidenceTotalDataDao() ;
		}
		
		return _instance ;
	}
	
	public List<SubsidenceTotalData> queryAllSection(){
		
		String sql = "select * from SubsidenceTotalData" ;
		
		return mDatabase.queryObjects(sql, SubsidenceTotalData.class) ;
	}
}
