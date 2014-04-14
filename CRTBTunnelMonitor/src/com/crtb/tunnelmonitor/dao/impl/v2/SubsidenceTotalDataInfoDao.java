package com.crtb.tunnelmonitor.dao.impl.v2;

import com.crtb.tunnelmonitor.entity.SubsidenceTotalDataInfo;

public class SubsidenceTotalDataInfoDao extends AbstractDao<SubsidenceTotalDataInfo> {

	private static SubsidenceTotalDataInfoDao _instance ;
	
	private SubsidenceTotalDataInfoDao(){
		
	}
	
	public static SubsidenceTotalDataInfoDao defaultDao(){
		
		if(_instance == null){
			_instance	= new SubsidenceTotalDataInfoDao() ;
		}
		
		return _instance ;
	}
}
