package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import com.crtb.tunnelmonitor.entity.ControlPointsInfo;

public class ControlPointsInfoDao extends AbstractDao<ControlPointsInfo> {

	private static ControlPointsInfoDao _instance ;
	
	private ControlPointsInfoDao(){
		
	}
	
	public static ControlPointsInfoDao defaultDao(){
		
		if(_instance == null){
			_instance	= new ControlPointsInfoDao() ;
		}
		
		return _instance ;
	}
	
	public List<ControlPointsInfo> queryAllControlPoints() {
		String sql = "select * from ControlPointsInfo";
		return mDatabase.queryObjects(sql, ControlPointsInfo.class);
	}
	
}
