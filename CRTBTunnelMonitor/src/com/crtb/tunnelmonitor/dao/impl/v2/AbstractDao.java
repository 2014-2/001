package com.crtb.tunnelmonitor.dao.impl.v2;

import org.zw.android.framework.IAccessDatabase;
import org.zw.android.framework.IFrameworkFacade;
import org.zw.android.framework.impl.FrameworkFacade;
import org.zw.android.framework.util.StringUtils;

import android.util.Log;

import com.crtb.tunnelmonitor.AppPreferences;

/**
 * abstract dao
 * 
 * @author zhouwei
 *
 */
public abstract class AbstractDao<T> {
	
	static final String TAG						= "AbstractDao" ;
	static final String PROJECT_PREFIX			= "crtb_" ;

	private IFrameworkFacade	mFramework ;
	
	protected AbstractDao(){
		mFramework	= FrameworkFacade.getFrameworkFacade() ;
	}
	
	protected final String getDbUniqueName(String name){
		return PROJECT_PREFIX + name + ".db";
	}
	
	protected final IAccessDatabase getDefaultDb(){
		return mFramework.openDefaultDatabase();
	}
	
	protected final IAccessDatabase getCurrentDb(){
		
		// 当前项目名称
		String name = AppPreferences.getPreferences().getCurrentProject();

		if (StringUtils.isEmpty(name)) {
			
			Log.e(TAG, "zhouwei : 没有编辑项目");
			
			return null;
		}
		
		// return
		return getProjectIndexDb(name) ;
	}
	
	protected final IAccessDatabase getProjectIndexDb(String projectName){
		
		String dbName = getDbUniqueName(projectName);
		
		// return
		return mFramework.openDatabaseByName(dbName, 0) ;
	}
	
	public boolean insert(T bean){
		
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
	
	public boolean update(T bean){
		
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
	
	public boolean delete(T bean){
		
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
