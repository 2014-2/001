package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import org.zw.android.framework.IAccessDatabase;

import android.util.Log;

import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;
import com.crtb.tunnelmonitor.utils.AlertUtils;

/**
 * 全站仪
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
	
	public List<TunnelSettlementTotalData> queryAllOrderByMEASNoDesc() {
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return null ;
		}
		
		String sql = "select * from TunnelSettlementTotalData ORDER BY MEASNo DESC";
		
		return mDatabase.queryObjects(sql, TunnelSettlementTotalData.class);
	}

    public TunnelSettlementTotalData queryOneById(int id) {
       
    	final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return null;
        }

        String sql = "select * from TunnelSettlementTotalData where ID = ?";

        return mDatabase.queryObject(sql, new String[] { String.valueOf(id) },
                TunnelSettlementTotalData.class);

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
	public TunnelSettlementTotalData queryTunnelTotalData(int sheetId,int chainageId,String pntType) {
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return null ;
		}
		
		String sql = "select * from TunnelSettlementTotalData where SheetId = ? and ChainageId = ? and PntType = ? order by MEASNo desc" ;
		
		return mDatabase.queryObject(sql, new String[]{String.valueOf(sheetId),String.valueOf(chainageId),pntType},TunnelSettlementTotalData.class);
	}

	public List<TunnelSettlementTotalData> queryTunnelTotalDatas(int sheetId, int chainageId) {
		final IAccessDatabase mDatabase = getCurrentDb();
		if (mDatabase == null) {
			return null;
		}
		String sql = "select * from TunnelSettlementTotalData where SheetId = ? and ChainageId = ? order by MEASNo asc";
		return mDatabase.queryObjects(sql, new String[] { String.valueOf(sheetId), String.valueOf(chainageId) }, TunnelSettlementTotalData.class);
	}
	
    /**
     * 查询 本次测量(MEASNo)之前的所有相同断面和相同测点类型的测点信息
     * 
     * @param chainageId
     *            断面里程ID
     * @param pntType
     *            测点类型
     * @param MEASNo
     *            本次测量是第几次测量
     * @return 查询到的测点信息List
     */
    public List<TunnelSettlementTotalData> queryInfoBeforeMEASNo(
            int chainageId, String pntType, int MEASNo) {

        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return null;
        }

        String sql = "select * from TunnelSettlementTotalData where chainageId="
                + chainageId
                + " AND pntType=\'"
                + pntType
                + "\' AND MEASNo<?"
                + " AND DataStatus != ?"
                + " order by MEASNo ASC";

        String[] args = new String[] {String.valueOf(MEASNo), String.valueOf(AlertUtils.POINT_DATASTATUS_DISCARD)};
        return mDatabase.queryObjects(sql, args, TunnelSettlementTotalData.class);
    }

    /**
     * 查询 本次测量(MEASNo)之后的所有相同断面和相同测点类型的测点信息
     * 
     * @param chainageId
     *            断面里程ID
     * @param pntType
     *            测点类型
     * @param MEASNo
     *            本次测量是第几次测量
     * @return 查询到的测点信息List
     */
    public List<TunnelSettlementTotalData> queryInfoAfterMEASNo(int chainageId, String pntType,
            int MEASNo) {

        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return null;
        }

        String sql = "select * from TunnelSettlementTotalData where chainageId=" + chainageId
                + " AND pntType=\'" + pntType + "\' AND MEASNo>?" + " AND DataStatus != ?"
                + " order by MEASNo ASC";

        String[] args = new String[] { String.valueOf(MEASNo),
                String.valueOf(AlertUtils.POINT_DATASTATUS_DISCARD) };
        return mDatabase.queryObjects(sql, args, TunnelSettlementTotalData.class);
    }

    public TunnelSettlementTotalData queryOppositePointOfALine(TunnelSettlementTotalData point1,
            String point2Type) {
    	
    	final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return null ;
		}
		
        TunnelSettlementTotalData point2 = null;
        String sql = "select * from TunnelSettlementTotalData where chainageId="
                // 同一个断面
                + point1.getChainageId()
                // 同一次测量
                + " AND MEASNo=?"
                + " AND pntType=\'" + point2Type + "\'";

        List<TunnelSettlementTotalData> list = mDatabase.queryObjects(sql,
                new String[] { String.valueOf(point1.getMEASNo()) },
                TunnelSettlementTotalData.class);

        if (list != null && list.size() == 1) {
            point2 = list.get(0);
        }

        return point2;
    }

    public void updateDataStatus(int id, int dataStatus, float correction) {
        Log.d(TAG, "TunnelSettlementTotalData updateDataStatus");
        IAccessDatabase db = getCurrentDb();
        if (db != null) {
            String sql = "UPDATE TunnelSettlementTotalData"
                    + " SET DataStatus=" + dataStatus
                    + ((dataStatus == AlertUtils.POINT_DATASTATUS_CORRECTION) ? (", DataCorrection=" + correction) : "")
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
	
}
