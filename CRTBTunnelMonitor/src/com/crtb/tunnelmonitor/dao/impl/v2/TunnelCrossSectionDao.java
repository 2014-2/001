package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import com.crtb.tunnelmonitor.entity.TunnelCrossSectionInfo;

/**
 * 
 * @author zhouwei
 *
 */
public final class TunnelCrossSectionDao extends AbstractDao<TunnelCrossSectionInfo> {

	private static TunnelCrossSectionDao _instance ;
	
	private TunnelCrossSectionDao(){
		
	}
	
	public static TunnelCrossSectionDao defaultDao(){
		
		if(_instance == null){
			_instance	= new TunnelCrossSectionDao() ;
		}
		
		return _instance ;
	}
	
	public List<TunnelCrossSectionInfo> queryAllSection(){
		
		String sql = "select * from TunnelCrossSectionInfo" ;
		
		return mDatabase.queryObjects(sql, TunnelCrossSectionInfo.class) ;
	}
}
