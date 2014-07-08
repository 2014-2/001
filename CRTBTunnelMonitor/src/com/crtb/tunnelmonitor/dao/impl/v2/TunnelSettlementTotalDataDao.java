package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import org.zw.android.framework.IAccessDatabase;
import org.zw.android.framework.db.core.SQLiteParamUtils;
import org.zw.android.framework.util.StringUtils;

import android.util.Log;

import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;
import com.crtb.tunnelmonitor.utils.AlertUtils;

/**
 * 隧道内测量数据
 * 
 * @author zhouwei
 *
 */
public class TunnelSettlementTotalDataDao extends AbstractDao<TunnelSettlementTotalData> {

	private static TunnelSettlementTotalDataDao _instance ;
	
	private TunnelSettlementTotalDataDao(){
		
	}
	
	public static TunnelSettlementTotalDataDao defaultDao(){
		
		if(_instance == null){
			_instance	= new TunnelSettlementTotalDataDao() ;
		}
		
		return _instance ;
	}
	
	public void reset(TunnelSettlementTotalData bean){
		
		if(bean == null){
			return ;
		}
		
		final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return;
        }
		
		String sql = "update TunnelSettlementTotalData set Coordinate = \"\", SurveyTime = \"\" ,DataStatus = 0 ,MEASNo = 0 ,Info = \"\" where ID = ? ";
		
		mDatabase.execute(sql, SQLiteParamUtils.toParamemter(bean.getID()));
	}

    public List<TunnelSettlementTotalData> queryAllOrderByMeasIdDesc(String pntType) {

        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return null;
        }

        String sql = "select * from TunnelSettlementTotalData where PntType=? ORDER BY ID DESC";
        String[] args = SQLiteParamUtils.toParamemter(pntType);

        return mDatabase.queryObjects(sql, args,TunnelSettlementTotalData.class);
    }

    public TunnelSettlementTotalData queryOneById(int id) {
       
    	final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return null;
        }

        String sql = "select * from TunnelSettlementTotalData where ID = ?";

        return mDatabase.queryObject(sql, SQLiteParamUtils.toParamemter(id), TunnelSettlementTotalData.class);
    }
    
    public TunnelSettlementTotalData queryOneByGuid(String guid) {
        
        final IAccessDatabase mDatabase = getCurrentDb();
        
        if (mDatabase == null || guid == null) {
            return null;
        }
        
        String sql = "select * from TunnelSettlementTotalData where Guid = ?";
        
        return mDatabase.queryObject(sql, new String[] {guid}, TunnelSettlementTotalData.class);
    }
    
    // 删除测量单的所有测量数据
    public void removeTotalDataBySheetId(int sheetid){
    	
    	final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return;
        }
        
        String sql = "delete from TunnelSettlementTotalData where SheetId = ?";
        
        mDatabase.execute(sql, new String[]{String.valueOf(sheetid)});
    }

    // 查询已经存在的测量点信息
	public TunnelSettlementTotalData queryTunnelTotalData(String sheetId,String chainageId,String pntType) {
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return null ;
		}
		
		String sql 	= "select * from TunnelSettlementTotalData where SheetId = ? and ChainageId = ? and PntType = ? order by ID desc" ;
		String[] args = SQLiteParamUtils.toParamemter(sheetId,chainageId,pntType);
		
		return mDatabase.queryObject(sql, args,TunnelSettlementTotalData.class);
	}

	// 查询本记录单下，对应断面的所有测量数据
	public List<TunnelSettlementTotalData> queryTunnelTotalDatas(String sheetId, String chainageId) {
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if (mDatabase == null) {
			return null;
		}
		
		String sql = "select * from TunnelSettlementTotalData where SheetId = ? and ChainageId = ? order by ID asc";
		String[] args = SQLiteParamUtils.toParamemter(sheetId,chainageId);
		
		return mDatabase.queryObjects(sql, args, TunnelSettlementTotalData.class);
	}
	
	// 是否存在测量数据
	public boolean checkRawSheetIndexHasData(int sheetId) {
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if (mDatabase == null) {
			return false;
		}
		
		String sql = "select * from TunnelSettlementTotalData where SheetId = ?";
		String[] args = SQLiteParamUtils.toParamemter(sheetId);
		
		List<TunnelSettlementTotalData> list = mDatabase.queryObjects(sql, args, TunnelSettlementTotalData.class) ;
		
		if(list == null || list.isEmpty()){
			return false;
		}
		
		for(TunnelSettlementTotalData data : list){
			
			if(!StringUtils.isEmpty(data.getCoordinate())){
				return true ;
			}
		}
		
		return false ;
	}
	
    /**
     * 查询 本次测量(MEASNo)之前的所有相同断面和相同测点类型的测点信息
     * 
     * @param chainageId
     *            断面里程ID
     * @param pntType
     *            测点类型
     * @param id
     *            本次测量是第几次测量
     * @return 查询到的测点信息List
     */
    public List<TunnelSettlementTotalData> queryInfoBeforeMeasId(
            String chainageId, String pntType, int id) {

        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return null;
        }

        int firstLineID = queryAsFirstLineDataID(chainageId, pntType, id);
        String sql = "select * from TunnelSettlementTotalData where chainageId=\'"
                + chainageId + "\'"
                + " AND pntType=\'"
                + pntType
                + "\' AND ID<?"
                + " AND ID>=?"
                + " AND DataStatus != ?"
                + " order by ID ASC";

        String[] args = new String[] {String.valueOf(id), String.valueOf(firstLineID), String.valueOf(AlertUtils.POINT_DATASTATUS_DISCARD)};
        
        return mDatabase.queryObjects(sql, args, TunnelSettlementTotalData.class);
    }

    /**
     * 查询 本次测量(MEASNo)及之后的所有相同断面和相同测点类型的测点信息
     * 
     * @param chainageId
     *            断面里程ID
     * @param pntType
     *            测点类型
     * @param id
     *            本次测量是第几次测量
     * @return 查询到的测点信息List
     */
    public List<TunnelSettlementTotalData> queryInfoAfterMeasId(String chainageId, String pntType,
            int id) {

        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return null;
        }

        String sql = "select * from TunnelSettlementTotalData where chainageId=\'" + chainageId + "\'"
                + " AND pntType=\'" + pntType + "\' AND ID>=?" + " AND DataStatus != ?"
                + " order by ID ASC";

        String[] args = new String[] { String.valueOf(id),
                String.valueOf(AlertUtils.POINT_DATASTATUS_DISCARD) };
        
        return mDatabase.queryObjects(sql, args, TunnelSettlementTotalData.class);
    }

    public TunnelSettlementTotalData queryOppositePointOfALine(TunnelSettlementTotalData point1, String point2Type) {
    	
    	final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return null ;
		}
		
        TunnelSettlementTotalData point2 = null;
        String sql = "select * from TunnelSettlementTotalData where chainageId=?"
                // 同一个断面
                // 同一次测量
                + " AND sheetid=?"
                + " AND pntType=?";

        List<TunnelSettlementTotalData> list = mDatabase.queryObjects(sql,
                new String[] { point1.getChainageId(), point1.getSheetId(), point2Type },
                TunnelSettlementTotalData.class);

        if (list != null && list.size() == 1) {
            point2 = list.get(0);
        }

        return point2;
    }

    public void updateDataStatus(String guid, int dataStatus, float correction) {
        
        Log.d(TAG, "TunnelSettlementTotalData updateDataStatus");
        
        IAccessDatabase db = getCurrentDb();
        
        if (db != null) {
            
            String sql = "UPDATE TunnelSettlementTotalData"
                    + " SET DataStatus=" + dataStatus
                    + ", DataCorrection=" + ((dataStatus == AlertUtils.POINT_DATASTATUS_CORRECTION) ? correction : 0f)
                    + " WHERE Guid=?";
            
            String[] args = new String[]{guid};
            
            db.execute(sql, args);
        }
    }

    public void updateDataStatus(int id, int dataStatus, float correction) {
    	
        Log.d(TAG, "TunnelSettlementTotalData updateDataStatus");
        
        IAccessDatabase db = getCurrentDb();
        
        if (db != null) {
        	
            String sql = "UPDATE TunnelSettlementTotalData"
                    + " SET DataStatus=" + dataStatus
                    + ", DataCorrection=" + ((dataStatus == AlertUtils.POINT_DATASTATUS_CORRECTION) ? correction : 0f)
                    + " WHERE ID=?";
            
            String[] args = new String[]{String.valueOf(id)};
            
            db.execute(sql, args);
        }
    }
    
	public List<TunnelSettlementTotalData> queryUnUploadTunnelTotalDataBySheet(int sheetId) {
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if (mDatabase == null) {
			return null;
		}
		
		String sql = "select * from TunnelSettlementTotalData where SheetId = " + sheetId + " and Info = '1'";
		
		return mDatabase.queryObjects(sql, TunnelSettlementTotalData.class);
	}
	
	public List<TunnelSettlementTotalData> queryTunnelTotalDataSection(String guid) {
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if (mDatabase == null) {
			return null;
		}
		
		String sql = "select * from TunnelSettlementTotalData where ChainageId = ? limit 0,1 ";
		
		return mDatabase.queryObjects(sql, SQLiteParamUtils.toParamemter(guid),TunnelSettlementTotalData.class);
	}
	
	public List<TunnelSettlementTotalData> queryTunnelTotalDataSheet(String guid) {
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if (mDatabase == null) {
			return null;
		}
		
		String sql = "select * from TunnelSettlementTotalData where SheetId = ? limit 0,1 ";
		
		return mDatabase.queryObjects(sql, SQLiteParamUtils.toParamemter(guid),TunnelSettlementTotalData.class);
	}

	private int queryAsFirstLineDataID(
            String chainageId, String pntType, int id) {

	    int ret = -1;
        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return ret;
        }

        String sql = "select * from TunnelSettlementTotalData where chainageId=\'"
                + chainageId + "\'"
                + " AND pntType=\'"
                + pntType
                + "\' AND ID<?"
                + " AND DataStatus == ?"
                + " order by ID ASC";

        String[] args = new String[] {String.valueOf(id), String.valueOf(AlertUtils.POINT_DATASTATUS_AS_FIRSTLINE)};
        List<TunnelSettlementTotalData> list =  mDatabase.queryObjects(sql, args, TunnelSettlementTotalData.class);
        if (list != null & list.size() > 0) {
            for (TunnelSettlementTotalData data : list) {
                ret = Math.max(ret, data.getID());
            }
        }
        return ret;
	}
}
