package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import org.zw.android.framework.IAccessDatabase;

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
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return null ;
		}
		
		String sql = "select * from TunnelCrossSectionIndex" ;
		
		return mDatabase.queryObjects(sql, TunnelCrossSectionIndex.class) ;
	}
}
