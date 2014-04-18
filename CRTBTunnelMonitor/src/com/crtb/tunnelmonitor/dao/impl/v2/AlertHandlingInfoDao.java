package com.crtb.tunnelmonitor.dao.impl.v2;

import com.crtb.tunnelmonitor.entity.ControlPointsIndex;

public class AlertHandlingInfoDao extends AbstractDao<ControlPointsIndex> {

	private static AlertHandlingInfoDao _instance ;
	
	private AlertHandlingInfoDao(){
		
	}
	
	public static AlertHandlingInfoDao defaultDao(){
		
		if(_instance == null){
			_instance	= new AlertHandlingInfoDao() ;
		}
		
		return _instance ;
	}
}
