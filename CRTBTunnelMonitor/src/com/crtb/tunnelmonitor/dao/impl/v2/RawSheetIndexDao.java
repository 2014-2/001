package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import org.zw.android.framework.IAccessDatabase;
import org.zw.android.framework.util.StringUtils;

import android.util.Log;

import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.entity.SurveyerInformation;

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
	
	/**
	 * 保存测量人员信息
	 * @param bean
	 * @return
	 */
	public int insertSurveyer(SurveyerInformation bean) {
		
		if(bean == null){
			return DB_EXECUTE_FAILED ;
		}
		
		final IAccessDatabase db = getCurrentDb() ;
		
		if(db == null){
			
			Log.e("AbstractDao", "zhouwei : insert db is null");
			
			return DB_EXECUTE_FAILED ;
		}
		
		return db.saveObject(bean) > -1 ? DB_EXECUTE_SUCCESS : DB_EXECUTE_FAILED;
	}
	
	/**
	 * 查询测量人员
	 * 
	 * @param guid	: 记录单guid
	 * @return
	 */
	public SurveyerInformation querySurveyerBySheetIndexGuid(String guid){
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null || StringUtils.isEmpty(guid)){
			return null ;
		}
		
		String sql = "select * from SurveyerInformation where ProjectID = ?" ;
		
		return mDatabase.queryObject(sql, new String[]{guid}, SurveyerInformation.class);
	}

    public RawSheetIndex queryOneById(int id) {

        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return null;
        }

        String sql = "select * from RawSheetIndex where ID=?";
        String[] args = new String[] { String.valueOf(id) };
        return mDatabase.queryObject(sql, args, RawSheetIndex.class);
    }

    public RawSheetIndex queryOneByGuid(String guid) {

        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return null;
        }

        String sql = "select * from RawSheetIndex where Guid=?";
        String[] args = new String[] { guid };
        return mDatabase.queryObject(sql, args, RawSheetIndex.class);
    }
    
	// 隧道内记录单
	public List<RawSheetIndex> queryTunnelSectionRawSheetIndex() {
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return null ;
		}
		
		String sql = "select * from RawSheetIndex where CrossSectionType = ? ORDER BY CreateTime DESC";
		
		return mDatabase.queryObjects(sql, new String[]{String.valueOf(RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL)}, RawSheetIndex.class);
	}

	// 隧道内记录单
	public List<RawSheetIndex> queryTunnelSectionRawSheetIndexASC() {

		final IAccessDatabase mDatabase = getCurrentDb();

		if (mDatabase == null) {
			return null;
		}

		String sql = "select * from RawSheetIndex where CrossSectionType = ? ORDER BY CreateTime ASC";

		return mDatabase.queryObjects(sql, new String[] { String.valueOf(RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL) },
				RawSheetIndex.class);
	}

	// 地表下沉断面记录单
	public List<RawSheetIndex> queryAllSubsidenceSectionRawSheetIndex() {
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return null ;
		}
		
		String sql = "select * from RawSheetIndex where CrossSectionType = ? ORDER BY CreateTime DESC";
		
		return mDatabase.queryObjects(sql, new String[]{String.valueOf(RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES)}, RawSheetIndex.class);
	}
	
	// 地表下沉断面记录单
	public List<RawSheetIndex> queryAllSubsidenceSectionRawSheetIndexASC() {

		final IAccessDatabase mDatabase = getCurrentDb();

		if (mDatabase == null) {
			return null;
		}

		String sql = "select * from RawSheetIndex where CrossSectionType = ? ORDER BY CreateTime ASC";

		return mDatabase.queryObjects(sql,
				new String[] { String.valueOf(RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES) }, RawSheetIndex.class);
	}
	
	// 是否最新记录单
	public boolean isNewestRawSheetIndex(RawSheetIndex bean){
		
		if(bean == null){
			return false ;
		}
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return false ;
		}
		
		String sql = "select * from RawSheetIndex where CrossSectionType = ? ORDER BY CreateTime DESC limit 0,2";
		
		RawSheetIndex old = null ;
		
		if(bean.getCrossSectionType() == RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL){
			old	= mDatabase.queryObject(sql, new String[]{String.valueOf(RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL)}, RawSheetIndex.class);
		} else {
			old	= mDatabase.queryObject(sql, new String[]{String.valueOf(RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES)}, RawSheetIndex.class);
		}
		
		if(old == null){
			return false ;
		}
		
		return old.getID() == bean.getID() ;
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

	@Override
	public int delete(RawSheetIndex bean) {
		
		int code = super.delete(bean);
		
		if(code == DB_EXECUTE_SUCCESS){
			
			// 删除测量数据
			TunnelSettlementTotalDataDao.defaultDao().removeTotalDataBySheetId(bean.getID());
			SubsidenceTotalDataDao.defaultDao().removeSubsidenceTotalDataBySheetId(bean.getID());
			
			return DB_EXECUTE_SUCCESS ;
		}
		
		return DB_EXECUTE_FAILED;
	}
	
	public boolean isExistSectionGuidInLastRawSheet(String sectionGuid,int sectionType){	
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return false ;
		}
		
		String sql = "select * from RawSheetIndex where CrossSectionType = ? ORDER BY CreateTime DESC limit 1";
		RawSheetIndex last = null; 	
		if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL){
			last = mDatabase.queryObject(sql, new String[]{String.valueOf(RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL)}, RawSheetIndex.class);
		} else {
			last = mDatabase.queryObject(sql, new String[]{String.valueOf(RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES)}, RawSheetIndex.class);
		}
		
		if(last != null && last.getCrossSectionIDs().contains(sectionGuid)){
			return false;
		}
		
		return true;
	}
}
