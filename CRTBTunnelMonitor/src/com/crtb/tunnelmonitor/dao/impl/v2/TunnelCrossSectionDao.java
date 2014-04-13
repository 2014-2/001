package com.crtb.tunnelmonitor.dao.impl.v2;

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
}
