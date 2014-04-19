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
	
	public static final int DB_EXECUTE_SUCCESS	= 1 ; // 执行成功
	public static final int DB_EXECUTE_FAILED	= -1 ; //执行失败
	
	static final String TAG						= "AbstractDao" ;
	static final String PROJECT_PREFIX			= "crtb_" ;

	private IFrameworkFacade	mFramework ;
	
	protected AbstractDao(){
		mFramework	= FrameworkFacade.getFrameworkFacade() ;
	}
	
	public static final String getDbUniqueName(String name){
		return PROJECT_PREFIX + name + ".db";
	}
	
	protected final IAccessDatabase getDefaultDb(){
		return mFramework.openDefaultDatabase();
	}
	
	protected final IAccessDatabase getCurrentDb(){
		
		// 当前项目名称
		String name = AppPreferences.getPreferences().getCurrentProject();

		if (StringUtils.isEmpty(name)) {
			
			Log.e(TAG, "zhouwei : 当前没有编辑的工作面");
			
			return null;
		}
		
		// return
		return openDb(name) ;
	}
	
	protected final IAccessDatabase openDb(String dbName){
		// return
		return mFramework.openDatabaseByName(dbName, 0) ;
	}
	
	public int insert(T bean){
		
		if(bean == null){
			return DB_EXECUTE_FAILED ;
		}
		
		final IAccessDatabase db = getCurrentDb();
		
		if(db == null){
			
			Log.e("AbstractDao", "zhouwei : insert db is null");
			
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
			
			Log.e("AbstractDao", "zhouwei : update db is null");
			
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
			
			Log.e("AbstractDao", "zhouwei : delete db is null");
			
			return DB_EXECUTE_FAILED ;
		}
		
		return db.deleteObject(bean) > -1 ? DB_EXECUTE_SUCCESS : DB_EXECUTE_FAILED;
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
