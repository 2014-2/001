package com.crtb.tunnelmonitor.dao.impl.v2;

import com.crtb.tunnelmonitor.entity.RecordInfo;

/**
 * 
 * @author zhouwei
 *
 */
public final class RecordTunnelSecionDao extends AbstractDao<RecordInfo> {

	private static RecordTunnelSecionDao _instance ;
	
	private RecordTunnelSecionDao(){
		
	}
	
	public static RecordTunnelSecionDao defaultDao(){
		
		if(_instance == null){
			_instance	= new RecordTunnelSecionDao() ;
		}
		
		return _instance ;
	}
}
