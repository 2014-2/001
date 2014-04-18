package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;

/**
 * 全站仪
 * 
 * @author zhouwei
 *
 */
public class TunnelSettlementTotalDataDao extends AbstractDao<TunnelSettlementTotalData> {

	private static TunnelSettlementTotalDataDao _instance ;
	
	private TunnelSettlementTotalDataDao(){
		
	}
	
	public static TunnelSettlementTotalDataDao defaultDao(){
		
		if(_instance == null){
			_instance	= new TunnelSettlementTotalDataDao() ;
		}
		
		return _instance ;
	}
	
	public List<TunnelSettlementTotalData> queryAllRawSheetIndex() {
		
		String sql = "select * from TunnelSettlementTotalData";
		
		return mDatabase.queryObjects(sql, TunnelSettlementTotalData.class);
	}
}
