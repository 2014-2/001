package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import com.crtb.tunnelmonitor.entity.RecordInfo;

/**
 * 
 * @author zhouwei
 *
 */
public final class RecordTunnelSectionDao extends AbstractDao<RecordInfo> {

	private static RecordTunnelSectionDao _instance ;
	
	private RecordTunnelSectionDao(){
		
	}
	
	public static RecordTunnelSectionDao defaultDao(){
		
		if(_instance == null){
			_instance	= new RecordTunnelSectionDao() ;
		}
		
		return _instance ;
	}
	
	public List<RecordInfo> queryAllSection(){
		
		String sql = "select * from RecordInfo" ;
		
		return mDatabase.queryObjects(sql, RecordInfo.class) ;
	}
}
