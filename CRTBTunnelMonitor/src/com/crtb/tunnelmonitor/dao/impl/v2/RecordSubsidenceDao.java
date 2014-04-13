package com.crtb.tunnelmonitor.dao.impl.v2;

import com.crtb.tunnelmonitor.entity.RecordInfo;

/**
 * 
 * @author zhouwei
 *
 */
public final class RecordSubsidenceDao extends AbstractDao<RecordInfo> {

	private static RecordSubsidenceDao _instance ;
	
	private RecordSubsidenceDao(){
		
	}
	
	public static RecordSubsidenceDao defaultDao(){
		
		if(_instance == null){
			_instance	= new RecordSubsidenceDao() ;
		}
		
		return _instance ;
	}
}
