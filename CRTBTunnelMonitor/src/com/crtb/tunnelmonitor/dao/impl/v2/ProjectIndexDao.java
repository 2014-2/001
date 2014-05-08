package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.ArrayList;
import java.util.List;

import org.zw.android.framework.IAccessDatabase;

import android.util.Log;

import com.crtb.tunnelmonitor.AppPreferences;
import com.crtb.tunnelmonitor.entity.CrtbProject;
import com.crtb.tunnelmonitor.entity.CrtbUser;
import com.crtb.tunnelmonitor.entity.ProjectIndex;

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
			pro.setDbName(p.getDbName()); // 对应的数据库名称
			pro.setProjectName(p.getProjectName());
			pro.setCreateTime(p.getCreateTime());
			pro.setStartChainage(p.getStartChainage());
			pro.setEndChainage(p.getEndChainage());
			pro.setLastOpenTime(p.getLastOpenTime());
			pro.setInfo(p.getInfo());
			
			pro.setChainagePrefix(p.getChainagePrefix());
			
			pro.setGDLimitVelocity(p.getGDLimitVelocity());
			pro.setGDLimitTotalSettlement(p.getGDLimitTotalSettlement());
			pro.setGDCreateTime(p.getGDCreateTime());
			pro.setGDInfo(p.getGDInfo());
			
			pro.setSLLimitVelocity(p.getSLLimitVelocity());
			pro.setSLLimitTotalSettlement(p.getSLLimitTotalSettlement());
			pro.setSLCreateTime(p.getSLCreateTime());
			pro.setSLInfo(p.getSLInfo());
			
			pro.setDBLimitVelocity(p.getDBLimitVelocity());
			pro.setDBLimitTotalSettlement(p.getDBLimitTotalSettlement());
			pro.setDBCreateTime(p.getDBCreateTime());
			pro.setDBInfo(p.getDBInfo());
			
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
	
	public void updateCurrentWorkPlan(ProjectIndex bean){
		AppPreferences.getPreferences().putCurrentProject(bean.getDbName());
	}
	
	public boolean hasWorkPlan(){
		
		List<ProjectIndex> list = queryAllWorkPlan() ;
		
		return list != null && list.size() > 0 ;
	}
	
	public boolean hasExport(){
		
		final CrtbUser user = CrtbLicenseDao.defaultDao().queryCrtbUser() ;
		
		if(user == null){
			return false ;
		}
		
		// 非注册用户
		if(user.getUsertype() == CrtbUser.LICENSE_TYPE_DEFAULT){
			return false ;
		}
		
		return hasWorkPlan();
	}
	
	// 导入数据库
	public int inportDb(String dbName){
		
		final CrtbUser user = CrtbLicenseDao.defaultDao().queryCrtbUser() ;
		
		if(user == null){
			return DB_EXECUTE_FAILED ;
		}
		
		// 非注册用户
		if(user.getUsertype() == CrtbUser.LICENSE_TYPE_DEFAULT){
			
			String sql = "select * from CrtbProject" ;
			
			List<CrtbProject> list = getDefaultDb().queryObjects(sql, CrtbProject.class);
			
			if(list != null && list.size() >= MAX_PROJECTINDEX){
				Log.e(TAG, "error : 非注册用户,不能保存2个以上的工作面");
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
			pro.setGDCreateTime(obj.getGDCreateTime());
			pro.setGDInfo(obj.getGDInfo());
			
			pro.setSLLimitVelocity(obj.getSLLimitVelocity());
			pro.setSLLimitTotalSettlement(obj.getSLLimitTotalSettlement());
			pro.setSLCreateTime(obj.getSLCreateTime());
			pro.setSLInfo(obj.getSLInfo());
			
			pro.setDBLimitVelocity(obj.getDBLimitVelocity());
			pro.setDBLimitTotalSettlement(obj.getDBLimitTotalSettlement());
			pro.setDBCreateTime(obj.getDBCreateTime());
			pro.setDBInfo(obj.getDBInfo());
			
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
		
		// 非注册用户
		if(user.getUsertype() == CrtbUser.LICENSE_TYPE_DEFAULT){
			
			String sql = "select * from CrtbProject where username = ?" ;
			
			List<CrtbProject> list = getDefaultDb().queryObjects(sql, new String[]{user.getUsername()}, CrtbProject.class);
			
			if(list != null && list.size() >= MAX_PROJECTINDEX){
				Log.e(TAG, "error : 非注册用户,不能保存2个以上的工作面");
				return 100 ;
			}
		}
		
		final IAccessDatabase db = openDb(bean.getDbName());
		
		// 保存到对应的数据库
		if(db.saveObject(bean) > 0){
			
			ProjectIndex obj = db.queryObject("select * from ProjectIndex where ProjectName = ? ", new String[]{bean.getProjectName()}, ProjectIndex.class) ;
			
			CrtbProject pro = new CrtbProject() ;
			pro.setUsername(user.getUsername());
			
			// 第一次保存工作面对应的数据库名称
			pro.setDbName(obj.getDbName());
			
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
			pro.setGDCreateTime(obj.getGDCreateTime());
			pro.setGDInfo(obj.getGDInfo());
			
			pro.setSLLimitVelocity(obj.getSLLimitVelocity());
			pro.setSLLimitTotalSettlement(obj.getSLLimitTotalSettlement());
			pro.setSLCreateTime(obj.getSLCreateTime());
			pro.setSLInfo(obj.getSLInfo());
			
			pro.setDBLimitVelocity(obj.getDBLimitVelocity());
			pro.setDBLimitTotalSettlement(obj.getDBLimitTotalSettlement());
			pro.setDBCreateTime(obj.getDBCreateTime());
			pro.setDBInfo(obj.getDBInfo());
			
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
		
		if(bean == null || bean.getDbName() == null){
			return DB_EXECUTE_FAILED ;
		}
		
		final IAccessDatabase db = openDb(bean.getDbName());
		
		// 保存到对应的数据库
		if(db.updateObject(bean) > 0){
			
			String sql 		= "select * from CrtbProject where dbName = ? " ;
			CrtbProject pro = getDefaultDb().queryObject(sql, new String[]{bean.getDbName()}, CrtbProject.class) ;
			
			if(pro == null){
				return DB_EXECUTE_FAILED ;
			}
			
			pro.setDbName(bean.getDbName());
			
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
			pro.setGDCreateTime(bean.getGDCreateTime());
			pro.setGDInfo(bean.getGDInfo());
			
			pro.setSLLimitVelocity(bean.getSLLimitVelocity());
			pro.setSLLimitTotalSettlement(bean.getSLLimitTotalSettlement());
			pro.setSLCreateTime(bean.getSLCreateTime());
			pro.setSLInfo(bean.getSLInfo());
			
			pro.setDBLimitVelocity(bean.getDBLimitVelocity());
			pro.setDBLimitTotalSettlement(bean.getDBLimitTotalSettlement());
			pro.setDBCreateTime(bean.getDBCreateTime());
			pro.setDBInfo(bean.getDBInfo());
			
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
		
		if(bean == null || bean.getDbName() == null){
			return DB_EXECUTE_FAILED ;
		}
		
		final IAccessDatabase db = openDb(bean.getDbName());
		
		if(db.deleteObject(bean) > 0){
			
			getDefaultDb().execute("delete from CrtbProject where dbName = ? ", new String[]{bean.getDbName()}) ;
			
			db.removeDatabse() ;
			
			return DB_EXECUTE_SUCCESS ;
		}
		
		return DB_EXECUTE_FAILED ;
	}
}
