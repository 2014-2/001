package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import com.crtb.tunnelmonitor.entity.RawSheetIndex;

/**
 * 全站仪
 * 
 * @author zhouwei
 *
 */
public class RawSheetIndexDao extends AbstractDao<RawSheetIndex> {

	private static RawSheetIndexDao _instance ;
	
	private RawSheetIndexDao(){
		
	}
	
	public static RawSheetIndexDao defaultDao(){
		
		if(_instance == null){
			_instance	= new RawSheetIndexDao() ;
		}
		
		return _instance ;
	}
	
	public List<RawSheetIndex> queryAllRawSheetIndex() {
		
		String sql = "select * from RawSheetIndex";
		
		return mDatabase.queryObjects(sql, RawSheetIndex.class);
	}
}
