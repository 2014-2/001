package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;

/**
 * 
 * @author zhouwei
 *
 */
public final class RecordTunnelSettlementTotalDataDao extends AbstractDao<TunnelSettlementTotalData> {

	private static RecordTunnelSettlementTotalDataDao _instance ;
	
	private RecordTunnelSettlementTotalDataDao(){
		
	}
	
	public static RecordTunnelSettlementTotalDataDao defaultDao(){
		
		if(_instance == null){
			_instance	= new RecordTunnelSettlementTotalDataDao() ;
		}
		
		return _instance ;
	}
	
	public List<TunnelSettlementTotalData> queryAllTunnelSection(){
		
		String sql = "select * from TunnelSettlementTotalData" ;
		
		return mDatabase.queryObjects(sql, TunnelSettlementTotalData.class) ;
	}
}
