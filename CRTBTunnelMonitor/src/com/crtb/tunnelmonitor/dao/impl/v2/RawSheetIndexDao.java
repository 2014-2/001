package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import org.zw.android.framework.IAccessDatabase;

import com.crtb.tunnelmonitor.entity.RawSheetIndex;

/**
 * 记录单信息表
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
	
	// 隧道内记录单
	public List<RawSheetIndex> queryTunnelSectionRawSheetIndex() {
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return null ;
		}
		
		String sql = "select * from RawSheetIndex where CrossSectionType = ? ";
		
		return mDatabase.queryObjects(sql, new String[]{String.valueOf(RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL)}, RawSheetIndex.class);
	}
	
	// 地表下沉断面记录单
	public List<RawSheetIndex> queryAllSubsidenceSectionRawSheetIndex() {
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return null ;
		}
		
		String sql = "select * from RawSheetIndex where CrossSectionType = ? ";
		
		return mDatabase.queryObjects(sql, new String[]{String.valueOf(RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES)}, RawSheetIndex.class);
	} 
	
	// 搜索记录单
	public List<RawSheetIndex> searchRawSheetIndex(String key,int type) {

		final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return null ;
		}
		
		String sql = "select * from RawSheetIndex where FACEDK like " + "'" + key + "%'" + " and CrossSectionType = ?";
		
		String[] param = {type == 0 ? String.valueOf(RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL) : String.valueOf(RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES)};
		
		return mDatabase.queryObjects(sql, param, RawSheetIndex.class);
	}
}
