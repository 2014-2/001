package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import org.zw.android.framework.IAccessDatabase;
import org.zw.android.framework.db.core.SQLiteParamUtils;
import org.zw.android.framework.util.StringUtils;

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
	
	public void reset(SubsidenceTotalData bean){
		
		if(bean == null){
			return ;
		}
		
		final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return;
        }
		
		String sql = "update SubsidenceTotalData set Coordinate = \"\", SurveyTime = \"\" ,DataStatus = 0 ,MEASNo = 0 ,Info = \"\" where ID = ? ";
		
		mDatabase.execute(sql, SQLiteParamUtils.toParamemter(bean.getID()));
	}

    public List<SubsidenceTotalData> queryAllOrderByIdDesc(String pntType) {

        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return null;
        }

        String sql = "select * from SubsidenceTotalData where PntType=? ORDER BY ID DESC";
        String[] args = new String[] { pntType };

        return mDatabase.queryObjects(sql, args, SubsidenceTotalData.class);
    }

	public boolean checkSectionTestData(String guid){
		
		final IAccessDatabase mDatabase = getCurrentDb();

		if (mDatabase == null) {
			return false;
		}
		
		String sql = "select * from SubsidenceTotalData where ChainageId = ? limit 0,1";
		
		SubsidenceTotalData data = mDatabase.queryObject(sql, new String[] {String.valueOf(guid) }, SubsidenceTotalData.class) ;
		 
		return data != null;
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
    
    public SubsidenceTotalData queryOneByGuid(String guid) {
        
        final IAccessDatabase mDatabase = getCurrentDb();
        
        if (mDatabase == null) {
            return null;
        }
        
        String sql = "select * from SubsidenceTotalData where Guid = ?";
        
        return mDatabase.queryObject(sql, new String[] { guid },
                SubsidenceTotalData.class);
    }
    
    // 删除测量单的所有测量数据
    public void removeSubsidenceTotalDataBySheetId(int sheetid){
    	
    	final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return;
        }
        
        String sql = "delete from SubsidenceTotalData where SheetId = ?";
        
        mDatabase.execute(sql, new String[]{String.valueOf(sheetid)});
    }

    // 查询已经存在的测量点信息
    public SubsidenceTotalData querySubsidenceTotalData(String sheetId, String chainageId, String pntType) {

        final IAccessDatabase mDatabase = getCurrentDb();

        if(mDatabase == null){
            return null ;
        }

        String sql = "select * from SubsidenceTotalData where SheetId = ? and ChainageId = ? and PntType = ? order by MEASNo desc" ;

        return mDatabase.queryObject(sql, new String[]{String.valueOf(sheetId),String.valueOf(chainageId),pntType},SubsidenceTotalData.class);
    }

    // 记录单下，该断面的测量数据
    public List<SubsidenceTotalData> querySubsidenceTotalDatas(String sheetId, String chainageId) {
    	
        final IAccessDatabase mDatabase = getCurrentDb();
        
        if (mDatabase == null) {
            return null;
        }
        
        String sql = "select * from SubsidenceTotalData where SheetId = ? and ChainageId = ? order by MEASNo asc";
        
        return mDatabase.queryObjects(sql, new String[] {String.valueOf(sheetId), String.valueOf(chainageId)}, SubsidenceTotalData.class);
    }

    /**
     * 是否存在测量数据 
     * @param guid
     * @return
     */
 	public boolean checkRawSheetIndexHasData(String guid) {
 		
 		final IAccessDatabase mDatabase = getCurrentDb();
 		
 		if (mDatabase == null) {
 			return false;
 		}
 		
 		String sql = "select * from SubsidenceTotalData where SheetId = ?";
 		String[] args = SQLiteParamUtils.toParamemter(guid);
 		
 		List<SubsidenceTotalData> list = mDatabase.queryObjects(sql, args, SubsidenceTotalData.class) ;
 		
 		if(list == null || list.isEmpty()){
 			return false;
 		}
 		
 		for(SubsidenceTotalData data : list){
 			
 			if(!StringUtils.isEmpty(data.getCoordinate())){
 				return true ;
 			}
 		}
 		
 		return false ;
 	}
 	
    /**
     * 查询 本次测量(MEASNo)之前的所有相同断面和相同测点类型的测点信息
     *
     * @param chainageId 断面里程ID
     * @param pntType 测点类型
     * @param id 本次测量是第几次测量
     * @return 查询到的测点信息List
     */
    public List<SubsidenceTotalData> queryInfoBeforeMeasId(String chainageId, String pntType,
            int id) {

        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return null;
        }

        String sql = "select * from SubsidenceTotalData where"
                + " chainageId=\'" + chainageId + "\'"
                + " AND pntType=\'" + pntType + "\'"
                + " AND ID < " + String.valueOf(id)
                + " AND DataStatus != "
                + String.valueOf(AlertUtils.POINT_DATASTATUS_DISCARD)
                + " order by ID ASC";

        return mDatabase.queryObjects(sql, SubsidenceTotalData.class);
    }

    /**
     * 查询 本次测量(MEASNo)及之后的所有相同断面和相同测点类型的测点信息
     *
     * @param chainageId 断面里程ID
     * @param pntType 测点类型
     * @param measId 本次测量是第几次测量
     * @return 查询到的测点信息List
     */
    public List<SubsidenceTotalData> queryInfoAfterMeasId(String chainageId, String pntType,
            int measId) {
        
        final IAccessDatabase mDatabase = getCurrentDb();
        
        if (mDatabase == null) {
            return null;
        }
        
        String sql = "select * from SubsidenceTotalData where"
                + " chainageId=\'" + chainageId + "\'"
                + " AND pntType=\'" + pntType + "\'"
                + " AND ID >= " + String.valueOf(measId)
                + " AND DataStatus != "
                + String.valueOf(AlertUtils.POINT_DATASTATUS_DISCARD)
                + " order by ID ASC";
        
        return mDatabase.queryObjects(sql, SubsidenceTotalData.class);
    }

    public void updateDataStatus(int id, int dataStatus, float correction) {
        IAccessDatabase db = getCurrentDb();
        if (db != null) {
            String sql = "UPDATE SubsidenceTotalData"
                    + " SET DataStatus=dataStatus"
                    + ", DataCorrection=" + ((dataStatus == AlertUtils.POINT_DATASTATUS_CORRECTION) ? correction : 0f)
                    + " WHERE ID=?";
            String[] args = new String[]{String.valueOf(id)};
            db.execute(sql, args);
        }
    }

    public void updateDataStatus(String guid, int dataStatus, float correction) {
        IAccessDatabase db = getCurrentDb();
        if (db != null) {
            String sql = "UPDATE SubsidenceTotalData"
                    + " SET DataStatus=dataStatus"
                    + ", DataCorrection=" + ((dataStatus == AlertUtils.POINT_DATASTATUS_CORRECTION) ? correction : 0f)
                    + " WHERE Guid=?";
            String[] args = new String[]{guid};
            db.execute(sql, args);
        }
    }
}
