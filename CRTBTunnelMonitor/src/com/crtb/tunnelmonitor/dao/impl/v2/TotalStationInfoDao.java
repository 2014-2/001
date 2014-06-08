package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import org.zw.android.framework.IAccessDatabase;

import com.crtb.tunnelmonitor.entity.TotalStationIndex;

/**
 * 全站仪
 * 
 * @author zhouwei
 *
 */ 
public class TotalStationInfoDao extends AbstractDao<TotalStationIndex> {

	private static TotalStationInfoDao _instance ;
	
	private TotalStationInfoDao(){
		
	}
	
	public static TotalStationInfoDao defaultDao(){
		
		if(_instance == null){
			_instance	= new TotalStationInfoDao() ;
		}
		
		return _instance ;
	}

    public TotalStationIndex queryOneById(int id) {
        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return null;
        }

        String sql = "select * from TotalStationIndex where ID=?";

        String[] args = new String[] { String.valueOf(id) };
        return mDatabase.queryObject(sql, args, TotalStationIndex.class);
    }

	public List<TotalStationIndex> queryAllTotalStations() {
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return null ;
		}
		
		String sql = "select * from TotalStationIndex";
		
		return mDatabase.queryObjects(sql, TotalStationIndex.class);
	}
}
