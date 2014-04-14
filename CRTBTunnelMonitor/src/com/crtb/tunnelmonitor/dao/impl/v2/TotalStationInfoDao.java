package com.crtb.tunnelmonitor.dao.impl.v2;

import com.crtb.tunnelmonitor.entity.TotalStationInfo;

public class TotalStationInfoDao extends AbstractDao<TotalStationInfo> {

	private static TotalStationInfoDao _instance ;
	
	private TotalStationInfoDao(){
		
	}
	
	public static TotalStationInfoDao defaultDao(){
		
		if(_instance == null){
			_instance	= new TotalStationInfoDao() ;
		}
		
		return _instance ;
	}
}
