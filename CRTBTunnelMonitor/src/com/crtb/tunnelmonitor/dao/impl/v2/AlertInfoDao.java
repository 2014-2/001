package com.crtb.tunnelmonitor.dao.impl.v2;

import com.crtb.tunnelmonitor.entity.AlertInfo;

public class AlertInfoDao extends AbstractDao<AlertInfo> {

	private static AlertInfoDao _instance ;
	
	private AlertInfoDao(){
		
	}
	
	public static AlertInfoDao defaultDao(){
		
		if(_instance == null){
			_instance	= new AlertInfoDao() ;
		}
		
		return _instance ;
	}
}
