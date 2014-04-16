package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

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
	
	public List<TotalStationInfo> queryAllTotalStations() {
		String sql = "select * from TotalStationInfo";
		return mDatabase.queryObjects(sql, TotalStationInfo.class);
	}
}
