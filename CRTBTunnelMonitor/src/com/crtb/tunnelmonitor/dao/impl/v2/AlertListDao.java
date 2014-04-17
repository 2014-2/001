package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import com.crtb.tunnelmonitor.entity.AlertList;

public class AlertListDao extends AbstractDao<AlertList> {

	private static AlertListDao _instance ;
	
	private AlertListDao(){
		
	}
	
	public static AlertListDao defaultDao(){
		
		if(_instance == null){
			_instance	= new AlertListDao() ;
		}
		
		return _instance ;
	}
	
	public List<AlertList> queryAllRawSheetIndex() {
		
		String sql = "select * from AlertList";
		
		return mDatabase.queryObjects(sql, AlertList.class);
	}
}
