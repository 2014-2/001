package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;

/**
 * 隧道内断面DAO
 * 
 * @author zhouwei
 *
 */
public final class TunnelCrossSectionIndexDao extends AbstractDao<TunnelCrossSectionIndex> {

	private static TunnelCrossSectionIndexDao _instance ;
	
	private TunnelCrossSectionIndexDao(){
		
	}
	
	public static TunnelCrossSectionIndexDao defaultDao(){
		
		if(_instance == null){
			_instance	= new TunnelCrossSectionIndexDao() ;
		}
		
		return _instance ;
	}
	
	public List<TunnelCrossSectionIndex> queryAllSection(){
		
		String sql = "select * from TunnelCrossSectionIndex" ;
		
		return mDatabase.queryObjects(sql, TunnelCrossSectionIndex.class) ;
	}
}
