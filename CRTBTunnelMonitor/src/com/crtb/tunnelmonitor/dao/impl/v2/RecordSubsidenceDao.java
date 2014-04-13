package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import com.crtb.tunnelmonitor.entity.RecordSubsidenceInfo;

/**
 * 
 * @author zhouwei
 *
 */
public final class RecordSubsidenceDao extends AbstractDao<RecordSubsidenceInfo> {

	private static RecordSubsidenceDao _instance ;
	
	private RecordSubsidenceDao(){
		
	}
	
	public static RecordSubsidenceDao defaultDao(){
		
		if(_instance == null){
			_instance	= new RecordSubsidenceDao() ;
		}
		
		return _instance ;
	}
	
	public List<RecordSubsidenceInfo> queryAllSection(){
		
		String sql = "select * from RecordSubsidenceInfo" ;
		
		return mDatabase.queryObjects(sql, RecordSubsidenceInfo.class) ;
	}
}
