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
		
		String sql = "select * from CrtbProject where username = ?" ;
		
		List<CrtbProject> list = mDatabase.queryObjects(sql, new String[]{user.getUsername()}, CrtbProject.class);
		
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
			pro.setGDLimitTotalSettlement(p.getDBLimitTotalSettlement());
			pro.setSLLimitVelocity(p.getSLLimitVelocity());
			pro.setSLLimitTotalSettlement(p.getSLLimitTotalSettlement());
			pro.setDBLimitVelocity(p.getDBLimitVelocity());
			pro.setDBLimitTotalSettlement(p.getDBLimitTotalSettlement());
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
		
		// 当前项目名称
		String name = AppPreferences.getPreferences().getCurrentProject();
		
		String sql = "select * from ProjectIndex where ProjectName = ? " ;
		
		return db.queryObject(sql, new String[]{name}, ProjectIndex.class);
	}
	
	public void updateCurrentWorkPlan(ProjectIndex bean){
		AppPreferences.getPreferences().putCurrentProject(bean.getProjectName());
	}
	
	public boolean hasWorkPlan(){
		
		List<ProjectIndex> list = queryAllWorkPlan() ;
		
		return list != null && list.size() > 0 ;
	}

	@Override
	public boolean insert(ProjectIndex bean) {
		
		final CrtbUser user = CrtbLicenseDao.defaultDao().queryCrtbUser() ;
		
		// 非注册用户
		if(user.getUsertype() == CrtbUser.LICENSE_TYPE_DEFAULT){
			
			String sql = "select * from CrtbProject where username = ?" ;
			
			List<CrtbProject> list = getDefaultDb().queryObjects(sql, new String[]{user.getUsername()}, CrtbProject.class);
			
			if(list != null && list.size() >= 4){
				Log.e(TAG, "error : 非注册用户,不能保存4个以上的工作面");
				return false ;
			}
		}
		
		final IAccessDatabase db = getCurrentDb() ;
		
		// 保存到对应的数据库
		if(db.saveObject(bean) > 0){
			
			ProjectIndex obj = db.queryObject("select * from ProjectIndex where ProjectName = ? ", new String[]{bean.getProjectName()}, ProjectIndex.class) ;
			
			CrtbProject pro = new CrtbProject() ;
			pro.setUsername(user.getUsername());
			pro.setDbName(getDbUniqueName(obj.getProjectName()));
			
			pro.setId(obj.getId());
			pro.setProjectName(obj.getProjectName());
			pro.setCreateTime(obj.getCreateTime());
			pro.setStartChainage(obj.getStartChainage());
			pro.setEndChainage(obj.getEndChainage());
			pro.setLastOpenTime(obj.getLastOpenTime());
			pro.setInfo(obj.getInfo());
			
			pro.setChainagePrefix(obj.getChainagePrefix());
			pro.setGDLimitVelocity(obj.getGDLimitVelocity());
			pro.setGDLimitTotalSettlement(obj.getDBLimitTotalSettlement());
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
				
				return false ;
			}
			
			return true ;
		}
		
		Log.e(TAG, "error :保存工作面失败 ");
		
		return false ;
	}

	@Override
	public boolean update(ProjectIndex bean) {
		
		final IAccessDatabase db = getCurrentDb() ;
		
		if(db == null){
			return false ;
		}
		
		// 保存到对应的数据库
		if(db.updateObject(bean) > 0){
			
			String unique 	= getDbUniqueName(bean.getProjectName()) ;
			String sql 		= "select * from CrtbProject where dbName = ? " ;
			CrtbProject pro = getDefaultDb().queryObject(sql, new String[]{unique}, CrtbProject.class) ;
			
			pro.setDbName(unique);
			
			pro.setProjectId(bean.getId());
			pro.setProjectName(bean.getProjectName());
			pro.setCreateTime(bean.getCreateTime());
			pro.setStartChainage(bean.getStartChainage());
			pro.setEndChainage(bean.getEndChainage());
			pro.setLastOpenTime(bean.getLastOpenTime());
			pro.setInfo(bean.getInfo());
			
			pro.setChainagePrefix(bean.getChainagePrefix());
			pro.setGDLimitVelocity(bean.getGDLimitVelocity());
			pro.setGDLimitTotalSettlement(bean.getDBLimitTotalSettlement());
			pro.setSLLimitVelocity(bean.getSLLimitVelocity());
			pro.setSLLimitTotalSettlement(bean.getSLLimitTotalSettlement());
			pro.setDBLimitVelocity(bean.getDBLimitVelocity());
			pro.setDBLimitTotalSettlement(bean.getDBLimitTotalSettlement());
			pro.setConstructionFirm(bean.getConstructionFirm());
			pro.setLimitedTotalSubsidenceTime(bean.getLimitedTotalSubsidenceTime());
			
			// 更新失败
			if(getDefaultDb().updateObject(pro) < 0){
				
				Log.e(TAG, "error :更新工作面失败 ");
				
				return false ;
			}
			
			return true ;
		}
		
		Log.e(TAG, "error :更新工作面失败 ");
		
		return false ;
	}

	@Override
	public boolean delete(ProjectIndex bean) {
		
		final IAccessDatabase db = getCurrentDb() ;
		
		if(db == null){
			return false ;
		}
		
		if(db.deleteObject(bean) > 0){
			
			String unique 	= getDbUniqueName(bean.getProjectName()) ;
			
			getDefaultDb().execute("delete * from CrtbProject where dbName = ? ", new String[]{unique}) ;
			
			return true ;
		}
		
		return false ;
	}
}
