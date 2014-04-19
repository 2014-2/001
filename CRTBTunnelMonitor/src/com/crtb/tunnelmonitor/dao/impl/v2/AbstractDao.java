package com.crtb.tunnelmonitor.dao.impl.v2;

import org.zw.android.framework.IAccessDatabase;
import org.zw.android.framework.IFrameworkFacade;
import org.zw.android.framework.impl.FrameworkFacade;
import org.zw.android.framework.util.StringUtils;

import android.util.Log;

import com.crtb.tunnelmonitor.entity.CrtbProject;

/**
 * abstract dao
 * 
 * @author zhouwei
 *
 */
public abstract class AbstractDao<T> {

	private IFrameworkFacade	mFramework ;
	
	protected AbstractDao(){
		mFramework	= FrameworkFacade.getFrameworkFacade() ;
	}
	
	protected final IAccessDatabase getDefaultDb(){
		return mFramework.openDefaultDatabase();
	}
	
	protected final IAccessDatabase getCurrentDb(){
		
		CrtbProject progject = CrtbLicenseDao.defaultDao().queryEditCrtbProject() ;
		
		if(progject == null || StringUtils.isEmpty(progject.getDbName())){
			
			Log.e("AbstractDao", "zhouwei : 没有查询到项目");
			
			return null ;
		}
		
		// return
		return mFramework.openDatabaseByName(progject.getDbName(), 0) ;
	}
	
	public final boolean insert(T bean){
		
		if(bean == null){
			return false ;
		}
		
		final IAccessDatabase db = getCurrentDb();
		
		if(db == null){
			
			Log.e("AbstractDao", "zhouwei : insert db is null");
			
			return false ;
		}
		
		return db.saveObject(bean) > -1 ;
	}
	
	public final boolean update(T bean){
		
		if(bean == null){
			return false ;
		}
		
		final IAccessDatabase db = getCurrentDb();
		
		if(db == null){
			
			Log.e("AbstractDao", "zhouwei : update db is null");
			
			return false ;
		}
		
		return db.updateObject(bean) > -1 ;
	}
	
	public final boolean delete(T bean){
		
		if(bean == null){
			return false ;
		}
		
		final IAccessDatabase db = getCurrentDb();
		
		if(db == null){
			
			Log.e("AbstractDao", "zhouwei : delete db is null");
			
			return false ;
		}
		
		return db.deleteObject(bean) > -1 ;
	} 
	
	public final void executeSql(String sql , String[] params){
		
		final IAccessDatabase db = getCurrentDb();
		
		if(db == null){
			
			Log.e("AbstractDao", "zhouwei : executeSql db is null");
			
			return ;
		}
		
		db.execute(sql, params);
	}
}
