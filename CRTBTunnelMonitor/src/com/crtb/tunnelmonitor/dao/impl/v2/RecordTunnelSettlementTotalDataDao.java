package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import org.zw.android.framework.IAccessDatabase;

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
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return null ;
		}
		
		String sql = "select * from TunnelSettlementTotalData" ;
		
		return mDatabase.queryObjects(sql, TunnelSettlementTotalData.class) ;
	}
}
