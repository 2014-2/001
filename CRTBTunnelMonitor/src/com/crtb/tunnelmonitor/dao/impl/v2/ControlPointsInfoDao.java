package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import com.crtb.tunnelmonitor.entity.ControlPointsIndex;

public class ControlPointsInfoDao extends AbstractDao<ControlPointsIndex> {

	private static ControlPointsInfoDao _instance ;
	
	private ControlPointsInfoDao(){
		
	}
	
	public static ControlPointsInfoDao defaultDao(){
		
		if(_instance == null){
			_instance	= new ControlPointsInfoDao() ;
		}
		
		return _instance ;
	}
	
	public List<ControlPointsIndex> queryAllControlPoints() {
		String sql = "select * from ControlPointsIndex";
		return mDatabase.queryObjects(sql, ControlPointsIndex.class);
	}
	
}
