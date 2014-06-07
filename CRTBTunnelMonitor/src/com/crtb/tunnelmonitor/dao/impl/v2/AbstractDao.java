package com.crtb.tunnelmonitor.dao.impl.v2;

import org.zw.android.framework.IAccessDatabase;
import org.zw.android.framework.IFrameworkFacade;
import org.zw.android.framework.impl.FrameworkFacade;
import org.zw.android.framework.util.StringUtils;

import android.util.Log;

import com.crtb.tunnelmonitor.AppConfig;
import com.crtb.tunnelmonitor.AppPreferences;
import com.crtb.tunnelmonitor.entity.AlertHandlingList;
import com.crtb.tunnelmonitor.entity.AlertList;
import com.crtb.tunnelmonitor.entity.ControlPointsIndex;
import com.crtb.tunnelmonitor.entity.ConvergenceSettlementArching;
import com.crtb.tunnelmonitor.entity.CrownSettlementARCHING;
import com.crtb.tunnelmonitor.entity.DTMSProjectVersion;
import com.crtb.tunnelmonitor.entity.DTMSVersion;
import com.crtb.tunnelmonitor.entity.ProjectSettingIndex;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.entity.SiteProjectMapping;
import com.crtb.tunnelmonitor.entity.StationInfoIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionExIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceSettlementARCHING;
import com.crtb.tunnelmonitor.entity.SubsidenceTotalData;
import com.crtb.tunnelmonitor.entity.SurveyerInformation;
import com.crtb.tunnelmonitor.entity.TotalStationIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionExIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;
import com.crtb.tunnelmonitor.entity.WorkSiteIndex;
import com.crtb.tunnelmonitor.utils.CrtbDbFileUtils;

/**
 * abstract dao
 * 
 * @author zhouwei
 *
 */
public abstract class AbstractDao<T> {
	
	public static final int DB_EXECUTE_SUCCESS	= 1 ; // 执行成功
	public static final int DB_EXECUTE_DOING	= -2 ; //执行中
	public static final int DB_EXECUTE_FAILED	= -1 ; //执行失败
	
	public static final int TRIAL_USER_MAX_PROJECTINDEX_COUNT	= 1 ; 	// 试用版用户最大工作面数
	public static final int TRIAL_USER_MAX_SECTION_COUNT		= 10 ; 	// 试用版用户最大断面数
	
	static final String TAG						= "AbstractDao" ;
	
	protected IFrameworkFacade	mFramework ;
	
	protected AbstractDao(){
		mFramework	= FrameworkFacade.getFrameworkFacade() ;
	}
	
	/**
	 * 加载表
	 * @param db
	 */
	protected void loadAllTable(IAccessDatabase db){
		
		if(db == null){
			return ;
		}
		
		db.createTable(DTMSVersion.class);
		db.createTable(DTMSProjectVersion.class);
		db.createTable(ProjectSettingIndex.class);
		
		db.createTable(TunnelCrossSectionIndex.class);
		db.createTable(TunnelCrossSectionExIndex.class);
		db.createTable(TunnelSettlementTotalData.class);
		db.createTable(SubsidenceTotalData.class);
		db.createTable(SubsidenceCrossSectionIndex.class);
		db.createTable(RawSheetIndex.class);
		db.createTable(ControlPointsIndex.class);
		db.createTable(SubsidenceCrossSectionExIndex.class);
		
		db.createTable(TotalStationIndex.class);
		db.createTable(AlertList.class);
		db.createTable(AlertHandlingList.class);
		db.createTable(CrownSettlementARCHING.class);
		db.createTable(ConvergenceSettlementArching.class);
		db.createTable(SubsidenceSettlementARCHING.class);
		db.createTable(SurveyerInformation.class);
		db.createTable(StationInfoIndex.class);
		db.createTable(ControlPointsIndex.class);
		
		db.createTable(SiteProjectMapping.class);
		db.createTable(WorkSiteIndex.class);
	}
	
	/**
	 * 加密数据库名称
	 * @param name
	 * @return
	 */
	public static final String getDbUniqueName(String name){
		return name + AppConfig.DB_SUFFIX ;
	}
	
	/**
	 * 临时数据库名称
	 * @return
	 */
	public static final String getDbUniqueTempName(String name){
		return name + AppConfig.DB_TEMP_SUFFIX ;
	}
	
	/**
	 * 默认数据库
	 * @return
	 */
	protected final IAccessDatabase getDefaultDb(){
		return mFramework.openDefaultDatabase();
	}
	
	/**
	 * 当前数据库
	 * @return
	 */
	protected synchronized final IAccessDatabase getCurrentDb(){
		
		// 当前项目名称
		String name = AppPreferences.getPreferences().getCurrentProject();

		if (StringUtils.isEmpty(name)) {
			
			Log.e(TAG, "zhouwei : getCurrentDb() 没有打开的工作面");
			
			return null;
		}
		
		int pos = name.lastIndexOf(".");
		
		// 数据库名称
		if(pos > 0){
			name = name.substring(0,pos) ;
		}
		
		// return
		return openDb(name) ;
	}
	
	/**
	 * 关闭当前数据库
	 */
	public final void closeCurrentDb(){
		
		// 当前项目名称
		String dbName = AppPreferences.getPreferences().getCurrentProject();

		if (StringUtils.isEmpty(dbName)) {
			return;
		}

		int pos = dbName.lastIndexOf(".");

		// 数据库名称
		if (pos > 0) {
			dbName = dbName.substring(0, pos);
		}
		
		// 缓存
		mFramework.removeDatabaseByName(dbName);
		
		// 关闭文件并重新加密
		CrtbDbFileUtils.closeDbFile(dbName);
	}
	
	/**
	 * 打开数据库
	 * 
	 * @param dbName
	 * @return
	 */
	protected final IAccessDatabase openDb(String dbName){
		
		String tempName = getDbUniqueTempName(dbName);
		
		// 是否存在临时数据库文件
		if(!CrtbDbFileUtils.checkDbTemp(dbName)){
			
			// 清除缓存
			mFramework.removeDatabaseByName(tempName);
			
			// 打开数据库
			String[] info = CrtbDbFileUtils.openDbFile(dbName);
			
			if(info == null){
				return null ;
			}
		}
		
		// open
		return mFramework.openDatabaseByName(tempName, 0) ;
	}
	
	public int insert(T bean){
		
		if(bean == null){
			return DB_EXECUTE_FAILED ;
		}
		
		final IAccessDatabase db = getCurrentDb();
		
		if(db == null){
			
			Log.e(TAG, "zhouwei : insert db is null");
			
			return DB_EXECUTE_FAILED ;
		}
		
		return db.saveObject(bean) > -1 ? DB_EXECUTE_SUCCESS : DB_EXECUTE_FAILED;
	}
	
	public int update(T bean){
		
		if(bean == null){
			return DB_EXECUTE_FAILED ;
		}
		
		final IAccessDatabase db = getCurrentDb();
		
		if(db == null){
			
			Log.e(TAG, "zhouwei : update db is null");
			
			return DB_EXECUTE_FAILED ;
		}
		
		return db.updateObject(bean) > -1 ? DB_EXECUTE_SUCCESS : DB_EXECUTE_FAILED;
	}
	
	public int delete(T bean){
		
		if(bean == null){
			return DB_EXECUTE_FAILED ;
		}
		
		final IAccessDatabase db = getCurrentDb();
		
		if(db == null){
			
			Log.e(TAG, "zhouwei : delete db is null");
			
			return DB_EXECUTE_FAILED ;
		}
		
		return db.deleteObject(bean) > -1 ? DB_EXECUTE_SUCCESS : DB_EXECUTE_FAILED;
	} 
	
	public void executeSql(String sql , String[] params){
		
		final IAccessDatabase db = getCurrentDb();
		
		if(db == null){
			
			Log.e(TAG, "zhouwei : executeSql db is null");
			
			return ;
		}
		
		db.execute(sql, params);
	}
}
