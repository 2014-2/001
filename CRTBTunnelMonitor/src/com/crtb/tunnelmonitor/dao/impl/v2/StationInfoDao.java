package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import com.crtb.tunnelmonitor.entity.StationInfo;

public class StationInfoDao extends AbstractDao<StationInfo> {

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
