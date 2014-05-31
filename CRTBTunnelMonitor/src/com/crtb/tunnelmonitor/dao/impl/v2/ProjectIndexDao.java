package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.ArrayList;
import java.util.List;

import org.zw.android.framework.IAccessDatabase;
import org.zw.android.framework.impl.FrameworkFacade;
import org.zw.android.framework.util.DateUtils;

import android.util.Log;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.AppPreferences;
import com.crtb.tunnelmonitor.entity.AlertHandlingList;
import com.crtb.tunnelmonitor.entity.AlertList;
import com.crtb.tunnelmonitor.entity.ControlPointsIndex;
import com.crtb.tunnelmonitor.entity.ConvergenceSettlementArching;
import com.crtb.tunnelmonitor.entity.CrownSettlementARCHING;
import com.crtb.tunnelmonitor.entity.CrtbProject;
import com.crtb.tunnelmonitor.entity.CrtbUser;
import com.crtb.tunnelmonitor.entity.DTMSProjectVersion;
import com.crtb.tunnelmonitor.entity.DTMSVersion;
import com.crtb.tunnelmonitor.entity.ProjectIndex;
import com.crtb.tunnelmonitor.entity.ProjectSettingIndex;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.entity.SiteProjectMapping;
import com.crtb.tunnelmonitor.entity.StationInfoIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionExIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceSettlementARCHING;
import com.crtb.tunnelmonitor.entity.SubsidenceTotalData;
import com.crtb.tunnelmonitor.entity.SurveyerInformation;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionExIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;
import com.crtb.tunnelmonitor.entity.WorkSiteIndex;

/**
 * 工程信息DAO(工作面)
 * 
 * @author zhouwei
 *
 */
public final class ProjectIndexDao extends AbstractDao<ProjectIndex> {
	
	static String TAG = "ProjectIndexDao" ;
	
	private static ProjectIndexDao _instance ;

	private ProjectIndexDao(){
		
	}
	
	public static ProjectIndexDao defaultWorkPlanDao(){
		
		if(_instance == null){
			_instance	= new ProjectIndexDao() ;
		}
		
		return _instance ;
	}
	
	public List<ProjectIndex> queryAllWorkPlan(){
		
		final IAccessDatabase mDatabase = getDefaultDb() ;
		final CrtbUser	user = CrtbLicenseDao.defaultDao().queryCrtbUser() ;
		
		if(user == null){
			Log.e("AbstractDao", "zhouwei : crtb user is null");
			return null ;
		}
		
		String sql = "select * from CrtbProject" ;
		
		List<CrtbProject> list = mDatabase.queryObjects(sql, CrtbProject.class);
		
		if(list == null){
			return null ;
		}
		
		List<ProjectIndex> temp = new ArrayList<ProjectIndex>();
		
		for(CrtbProject p : list){
			
			ProjectIndex pro = new ProjectIndex() ;
			
			pro.setId(p.getProjectId());
			pro.setProjectName(p.getProjectName());
			pro.setCreateTime(p.getCreateTime());
			pro.setStartChainage(p.getStartChainage());
			pro.setEndChainage(p.getEndChainage());
			pro.setLastOpenTime(p.getLastOpenTime());
			pro.setInfo(p.getInfo());
			
			pro.setChainagePrefix(p.getChainagePrefix());
			
			pro.setGDLimitVelocity(p.getGDLimitVelocity());
			pro.setGDLimitTotalSettlement(p.getGDLimitTotalSettlement());
			
			pro.setDBLimitVelocity(p.getDBLimitVelocity());
			pro.setDBLimitTotalSettlement(p.getDBLimitTotalSettlement());
			
			pro.setSLLimitTotalSettlement(p.getSLLimitTotalSettlement());
			pro.setSLLimitVelocity(p.getSLLimitVelocity());
			
			pro.setConstructionFirm(p.getConstructionFirm());
			pro.setLimitedTotalSubsidenceTime(p.getLimitedTotalSubsidenceTime());
			
			temp.add(pro);
		}
		
		return temp ;
	}
	
	public ProjectIndex queryEditWorkPlan(){
		
		// 当前项目名称
		final IAccessDatabase db = getCurrentDb();
		
		if(db == null){
			return null ;
		}
		
		String sql = "select * from ProjectIndex" ;
		
		return db.queryObject(sql, null,ProjectIndex.class);
	}
	
	public String getCurrentWorkDbPath(){
		
		// 当前项目名称
		final IAccessDatabase db = getCurrentDb();

		if (db == null) {
			return null;
		}
		
		return db.getDatabasePath() ;
	}
	
	public void removeProjectIndex(ProjectIndex bean){
		
		String dbName = getDbUniqueName(bean.getProjectName());
		
		String name = AppPreferences.getPreferences().getCurrentProject() ;
		
		if(name.equals(dbName)){
			AppPreferences.getPreferences().removeCurrentProject();
		}
	}
	
	public void updateCurrentWorkPlan(ProjectIndex bean){
		
		String dbName = getDbUniqueName(bean.getProjectName());
		
		AppPreferences.getPreferences().putCurrentProject(dbName);
		
		IAccessDatabase db = openDb(dbName);
		
		if(db != null){
			
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
	}
	
	public boolean hasWorkPlan(){
		
		List<ProjectIndex> list = queryAllWorkPlan() ;
		
		return list != null && list.size() > 0 ;
	}

    public boolean hasExport() {
        int userType = AppCRTBApplication.getInstance().getCurUserType();
        return userType == CrtbUser.LICENSE_TYPE_DEFAULT ? false
                : hasWorkPlan();
    }

	// 导入数据库
	public int importDb(String dbName){
		
		final CrtbUser user = CrtbLicenseDao.defaultDao().queryCrtbUser() ;
		
		if(user == null){
			return DB_EXECUTE_FAILED ;
		}

        int limit = 0;
        int userType = AppCRTBApplication.getInstance().getCurUserType();
        if (userType == CrtbUser.LICENSE_TYPE_DEFAULT) {
            Log.d(TAG, "未授权用户, 不能创建工作面！");
            return DB_EXECUTE_FAILED;
        } else if (userType == CrtbUser.LICENSE_TYPE_TRIAL) {
            limit = TRIAL_USER_MAX_PROJECTINDEX_COUNT;
        } else if (userType == CrtbUser.LICENSE_TYPE_REGISTERED) {
            limit = -1;
        }

		// 非注册用户
		if (limit != -1) {
			
			String sql = "select * from CrtbProject" ;
			
			List<CrtbProject> list = getDefaultDb().queryObjects(sql, CrtbProject.class);
			
			if(list != null && list.size() >= limit){
				Log.e(TAG, "error : 试用版用户,不能保存" + limit + "个以上的工作面");
				return 100 ;
			}
		}
		
		final IAccessDatabase db = openDb(dbName);
		
		String sql = "select * from ProjectIndex" ;
		
		ProjectIndex obj = db.queryObject(sql, null, ProjectIndex.class);
		
		if(obj != null){
			
			CrtbProject pro = new CrtbProject() ;
			pro.setUsername(user.getUsername());
			
			// 第一次保存工作面对应的数据库名称
			pro.setDbName(dbName);
			
			pro.setProjectId(obj.getId());
			pro.setProjectName(obj.getProjectName());
			pro.setCreateTime(obj.getCreateTime());
			pro.setStartChainage(obj.getStartChainage());
			pro.setEndChainage(obj.getEndChainage());
			pro.setLastOpenTime(obj.getLastOpenTime());
			pro.setInfo(obj.getInfo());
			
			pro.setChainagePrefix(obj.getChainagePrefix());
			
			pro.setGDLimitVelocity(obj.getGDLimitVelocity());
			pro.setGDLimitTotalSettlement(obj.getGDLimitTotalSettlement());
			
			pro.setSLLimitVelocity(obj.getSLLimitVelocity());
			pro.setSLLimitTotalSettlement(obj.getSLLimitTotalSettlement());
			
			pro.setDBLimitVelocity(obj.getDBLimitVelocity());
			pro.setDBLimitTotalSettlement(obj.getDBLimitTotalSettlement());
			
			pro.setConstructionFirm(obj.getConstructionFirm());
			pro.setLimitedTotalSubsidenceTime(obj.getLimitedTotalSubsidenceTime());
			
			// 保存失败
			if(getDefaultDb().saveObject(pro) < 0){
				
				db.execute("delete from ProjectIndex where Id = ", new String[]{String.valueOf(obj.getId())});
			
				return DB_EXECUTE_FAILED ;
			}
			
			return DB_EXECUTE_SUCCESS ;
		}
		
		return DB_EXECUTE_FAILED ;
	}

	@Override
	public int insert(ProjectIndex bean) {
		
		final CrtbUser user = CrtbLicenseDao.defaultDao().queryCrtbUser() ;
		
		if(user == null){
			return DB_EXECUTE_FAILED ;
		}

        int limit = 0;
        int userType = AppCRTBApplication.getInstance().getCurUserType();
        if (userType == CrtbUser.LICENSE_TYPE_DEFAULT) {
            Log.d(TAG, "未授权用户, 不能创建工作面！");
            return DB_EXECUTE_FAILED;
        } else if (userType == CrtbUser.LICENSE_TYPE_TRIAL) {
            limit = TRIAL_USER_MAX_PROJECTINDEX_COUNT;
        } else if (userType == CrtbUser.LICENSE_TYPE_REGISTERED) {
            limit = -1;
        }

        // 非注册用户
        if (limit != -1) {

            String sql = "select * from CrtbProject where username = ?";

            List<CrtbProject> list = getDefaultDb().queryObjects(sql,
                    new String[] { user.getUsername() }, CrtbProject.class);

            if (list != null && list.size() >= limit) {
                Log.e(TAG, "error : 试用版用户,不能保存" + limit + "个以上的工作面");
                return 100;
            }
        }

		final IAccessDatabase db = openDb(getDbUniqueName(bean.getProjectName()));
		
		// 保存到对应的数据库
		if(db.saveObject(bean) > 0){
			
			// 保存工程配置文件表
			ProjectSettingIndex setting = new ProjectSettingIndex() ;
			setting.setProjectName(bean.getProjectName());
			setting.setChainagePrefix(bean.getChainagePrefix());
			setting.setInfo(bean.getInfo());
			setting.setProjectID(bean.getId());
			setting.setYMDFormat(DateUtils.toDate(DateUtils.toDateString(bean.getCreateTime()), DateUtils.DATE_FORMAT));
			setting.setHMSFormat(DateUtils.toDate(DateUtils.toDateString(bean.getCreateTime()), DateUtils.DATE_FORMAT));
			db.saveObject(setting);
			
			ProjectIndex obj = db.queryObject("select * from ProjectIndex where ProjectName = ? ", new String[]{bean.getProjectName()}, ProjectIndex.class) ;
			
			CrtbProject pro = new CrtbProject() ;
			pro.setUsername(user.getUsername());
			
			// 第一次保存工作面对应的数据库名称
			pro.setDbName(getDbUniqueName(obj.getProjectName()));
			
			pro.setProjectId(obj.getId());
			pro.setProjectName(obj.getProjectName());
			pro.setCreateTime(obj.getCreateTime());
			pro.setStartChainage(obj.getStartChainage());
			pro.setEndChainage(obj.getEndChainage());
			pro.setLastOpenTime(obj.getLastOpenTime());
			pro.setInfo(obj.getInfo());
			
			pro.setChainagePrefix(obj.getChainagePrefix());
			
			pro.setGDLimitVelocity(obj.getGDLimitVelocity());
			pro.setGDLimitTotalSettlement(obj.getGDLimitTotalSettlement());
			
			pro.setSLLimitVelocity(obj.getSLLimitVelocity());
			pro.setSLLimitTotalSettlement(obj.getSLLimitTotalSettlement());
			
			pro.setDBLimitVelocity(obj.getDBLimitVelocity());
			pro.setDBLimitTotalSettlement(obj.getDBLimitTotalSettlement());
			
			pro.setConstructionFirm(obj.getConstructionFirm());
			pro.setLimitedTotalSubsidenceTime(obj.getLimitedTotalSubsidenceTime());
			
			// 保存失败
			if(getDefaultDb().saveObject(pro) < 0){
				
				Log.e(TAG, "error :保存工作面失败 ");
				
				db.execute("delete from ProjectIndex where Id = ", new String[]{String.valueOf(obj.getId())});
				
				return DB_EXECUTE_FAILED ;
			}
			
			return DB_EXECUTE_SUCCESS ;
		}
		
		Log.e(TAG, "error :保存工作面失败 ");
		
		return DB_EXECUTE_FAILED ;
	}

	@Override
	public int update(ProjectIndex bean) {
		
		if(bean == null || bean.getProjectName() == null){
			return DB_EXECUTE_FAILED ;
		}
		
		String dbName = getDbUniqueName(bean.getProjectName()) ;
		
		final IAccessDatabase db = openDb(dbName);
		
		// 保存到对应的数据库
		if(db.updateObject(bean) > 0){
			
			String sql 		= "select * from CrtbProject where dbName = ? " ;
			CrtbProject pro = getDefaultDb().queryObject(sql, new String[]{dbName}, CrtbProject.class) ;
			
			if(pro == null){
				return DB_EXECUTE_FAILED ;
			}
			
			pro.setDbName(dbName);
			
			pro.setProjectId(bean.getId());
			pro.setProjectName(bean.getProjectName());
			pro.setCreateTime(bean.getCreateTime());
			pro.setStartChainage(bean.getStartChainage());
			pro.setEndChainage(bean.getEndChainage());
			pro.setLastOpenTime(bean.getLastOpenTime());
			pro.setInfo(bean.getInfo());
			
			pro.setChainagePrefix(bean.getChainagePrefix());
			
			pro.setGDLimitVelocity(bean.getGDLimitVelocity());
			pro.setGDLimitTotalSettlement(bean.getGDLimitTotalSettlement());
			
			pro.setSLLimitVelocity(bean.getSLLimitVelocity());
			pro.setSLLimitTotalSettlement(bean.getSLLimitTotalSettlement());
			
			pro.setDBLimitVelocity(bean.getDBLimitVelocity());
			pro.setDBLimitTotalSettlement(bean.getDBLimitTotalSettlement());
			
			pro.setConstructionFirm(bean.getConstructionFirm());
			pro.setLimitedTotalSubsidenceTime(bean.getLimitedTotalSubsidenceTime());
			
			int code = getDefaultDb().updateObject(pro) ;
			
			// 更新失败
			if(code < 0){
				
				Log.e(TAG, "error :更新工作面失败 ");
				
				return DB_EXECUTE_FAILED ;
			}
			
			String[] param = new String[]{bean.getChainagePrefix()} ;
			
			// 更新所有表中里程前缀
			// 1. 隧道内断面
			db.execute("update TunnelCrossSectionIndex set ChainagePrefix = ?",param);
			// 2. 地表下沉断面
			db.execute("update SubsidenceCrossSectionIndex set ChainagePrefix = ?",param);
			// 3. 记录单
			db.execute("update RawSheetIndex set prefix = ?",param);
			
			return DB_EXECUTE_SUCCESS ;
		}
		
		Log.e(TAG, "error :更新工作面失败 ");
		
		return DB_EXECUTE_FAILED ;
	}

	@Override
	public int delete(ProjectIndex bean) {
		
		if(bean == null || bean.getProjectName() == null){
			return DB_EXECUTE_FAILED ;
		}
		
		String dbName = getDbUniqueName(bean.getProjectName()) ;
		
		final IAccessDatabase db = openDb(dbName);
		
		if(db.deleteObject(bean) > 0){
			
			getDefaultDb().execute("delete from CrtbProject where dbName = ? ", new String[]{dbName}) ;
			
			// 删除数据库缓存
			FrameworkFacade.getFrameworkFacade().removeDatabaseByName(dbName);
			
			// 删除数据库文件
			db.removeDatabse() ;
			
			return DB_EXECUTE_SUCCESS ;
		}
		
		return DB_EXECUTE_FAILED ;
	}
}
