package com.crtb.tunnelmonitor.dao.impl.v2;

import com.crtb.tunnelmonitor.entity.StationInfoIndex;

public class StationInfoDao extends AbstractDao<StationInfoIndex> {

	private static StationInfoDao _instance ;
	
	private StationInfoDao(){
		
	}
	
	public static StationInfoDao defaultDao(){
		
		if(_instance == null){
			_instance	= new StationInfoDao() ;
		}
		
		return _instance ;
	}
	
}
