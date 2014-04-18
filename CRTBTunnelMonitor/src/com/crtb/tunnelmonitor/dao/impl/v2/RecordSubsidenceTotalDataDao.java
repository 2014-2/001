package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import com.crtb.tunnelmonitor.entity.SubsidenceTotalData;

/**
 * 
 * @author zhouwei
 *
 */
public final class RecordSubsidenceTotalDataDao extends AbstractDao<SubsidenceTotalData> {

	private static RecordSubsidenceTotalDataDao _instance ;
	
	private RecordSubsidenceTotalDataDao(){
		
	}
	
	public static RecordSubsidenceTotalDataDao defaultDao(){
		
		if(_instance == null){
			_instance	= new RecordSubsidenceTotalDataDao() ;
		}
		
		return _instance ;
	}
	
	public List<SubsidenceTotalData> queryAllSection(){
		
		String sql = "select * from SubsidenceTotalData" ;
		
		return mDatabase.queryObjects(sql, SubsidenceTotalData.class) ;
	}
}
