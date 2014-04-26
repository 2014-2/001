package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import org.zw.android.framework.IAccessDatabase;

import com.crtb.tunnelmonitor.entity.SubsidenceTotalData;
import com.crtb.tunnelmonitor.utils.AlertUtils;

public class SubsidenceTotalDataDao extends AbstractDao<SubsidenceTotalData> {

	private static SubsidenceTotalDataDao _instance ;
	
	private SubsidenceTotalDataDao(){
		
	}
	
	public static SubsidenceTotalDataDao defaultDao(){
		
		if(_instance == null){
			_instance	= new SubsidenceTotalDataDao() ;
		}
		
		return _instance ;
	}
	
	public List<SubsidenceTotalData> queryAllSection(){
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return null ;
		}
		
		String sql = "select * from SubsidenceTotalData" ;
		
		return mDatabase.queryObjects(sql, SubsidenceTotalData.class) ;
	}

    public SubsidenceTotalData queryOneById(int id) {
        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return null;
        }

        String sql = "select * from SubsidenceTotalData where ID = ?";

        return mDatabase.queryObject(sql, new String[] { String.valueOf(id) },
                SubsidenceTotalData.class);
    }

	// 查询已经存在的测量点信息
	public SubsidenceTotalData queryTunnelTotalData(int sheetId,int chainageId,String pntType) {
			
		final IAccessDatabase mDatabase = getCurrentDb();
			
		if(mDatabase == null){
			return null ;
		}
			
		String sql = "select * from SubsidenceTotalData where SheetId = ? and ChainageId = ? and PntType = ? order by MEASNo desc" ;
			
		return mDatabase.queryObject(sql, new String[]{String.valueOf(sheetId),String.valueOf(chainageId),pntType},SubsidenceTotalData.class);
	}

    /**
     * 查询 本次测量(MEASNo)之前的所有相同断面和相同测点类型的测点信息
     *
     * @param chainageId 断面里程ID
     * @param pntType 测点类型
     * @param MEASNo 本次测量是第几次测量
     * @return 查询到的测点信息List
     */
    public List<SubsidenceTotalData> queryInfoBeforeMEASNo(int chainageId, String pntType,
            int MEASNo) {

        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return null;
        }

        String sql = "select * from SubsidenceTotalData where"
                + " chainageId=" + chainageId
                + " AND pntType=" + pntType
                + " AND MEASNo < " + String.valueOf(MEASNo)
                + " AND DataStatus != "
                + String.valueOf(AlertUtils.POINT_DATASTATUS_DISCARD)
                + " order by MEASNo ASC";
        
        return mDatabase.queryObjects(sql, SubsidenceTotalData.class);
    }

    /**
     * 查询 本次测量(MEASNo)之后的所有相同断面和相同测点类型的测点信息
     *
     * @param chainageId 断面里程ID
     * @param pntType 测点类型
     * @param MEASNo 本次测量是第几次测量
     * @return 查询到的测点信息List
     */
    public List<SubsidenceTotalData> queryInfoAfterMEASNo(int chainageId, String pntType,
            int MEASNo) {
        
        final IAccessDatabase mDatabase = getCurrentDb();
        
        if (mDatabase == null) {
            return null;
        }
        
        String sql = "select * from SubsidenceTotalData where"
                + " chainageId=" + chainageId
                + " AND pntType=" + pntType
                + " AND MEASNo > " + String.valueOf(MEASNo)
                + " AND DataStatus != "
                + String.valueOf(AlertUtils.POINT_DATASTATUS_DISCARD)
                + " order by MEASNo ASC";
        
        return mDatabase.queryObjects(sql, SubsidenceTotalData.class);
    }

    public void updateDataStatus(int id, int dataStatus, float correction) {
        IAccessDatabase db = getCurrentDb();
        if (db != null) {
            String sql = "UPDATE SubsidenceTotalData"
                    + " SET DataStatus=dataStatus"
                    + ((dataStatus == AlertUtils.POINT_DATASTATUS_CORRECTION) ? (", DataCorrection=" + correction) : "")
                    + " WHERE ID=?";
            String[] args = new String[]{String.valueOf(id)};
            db.execute(sql, args);
        }
    }
}
