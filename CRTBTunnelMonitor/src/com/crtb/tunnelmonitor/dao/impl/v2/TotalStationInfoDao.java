package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import com.crtb.tunnelmonitor.entity.TotalStationIndex;

/**
 * 全站仪
 * 
 * @author zhouwei
 *
 */
public class TotalStationInfoDao extends AbstractDao<TotalStationIndex> {

	private static TotalStationInfoDao _instance ;
	
	private TotalStationInfoDao(){
		
	}
	
	public static TotalStationInfoDao defaultDao(){
		
		if(_instance == null){
			_instance	= new TotalStationInfoDao() ;
		}
		
		return _instance ;
	}
	
	public List<TotalStationIndex> queryAllTotalStations() {
		
		String sql = "select * from TotalStationIndex";
		
		return mDatabase.queryObjects(sql, TotalStationIndex.class);
	}
}
