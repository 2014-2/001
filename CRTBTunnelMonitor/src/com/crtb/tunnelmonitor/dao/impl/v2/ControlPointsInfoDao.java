package com.crtb.tunnelmonitor.dao.impl.v2;

import com.crtb.tunnelmonitor.entity.AlertInfo;

public class ControlPointsInfoDao extends AbstractDao<AlertInfo> {

	private static ControlPointsInfoDao _instance ;
	
	private ControlPointsInfoDao(){
		
	}
	
	public static ControlPointsInfoDao defaultDao(){
		
		if(_instance == null){
			_instance	= new ControlPointsInfoDao() ;
		}
		
		return _instance ;
	}
}
