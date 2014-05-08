package com.crtb.tunnelmonitor.dao.impl.v2;

import org.zw.android.framework.IAccessDatabase;

import android.util.Log;

/**
 * abstract dao
 * 
 * @author zhouwei
 *
 */
public abstract class AbstractDefaultDBDao<T> extends AbstractDao<T> {
	
	static final String TAG						= "AbstractDefaultDBDao" ;
	
	protected AbstractDefaultDBDao(){
		
	}
	
	@Override
	public int insert(T bean){
		
		if(bean == null){
			return DB_EXECUTE_FAILED ;
		}
		
		final IAccessDatabase db = getDefaultDb();
		
		if(db == null){
			
			Log.e(TAG, "zhouwei : insert db is null");
			
			return DB_EXECUTE_FAILED ;
		}
		
		return db.saveObject(bean) > -1 ? DB_EXECUTE_SUCCESS : DB_EXECUTE_FAILED;
	}
	
	@Override
	public int update(T bean){
		
		if(bean == null){
			return DB_EXECUTE_FAILED ;
		}
		
		final IAccessDatabase db = getDefaultDb();
		
		if(db == null){
			
			Log.e(TAG, "zhouwei : update db is null");
			
			return DB_EXECUTE_FAILED ;
		}
		
		return db.updateObject(bean) > -1 ? DB_EXECUTE_SUCCESS : DB_EXECUTE_FAILED;
	}
	
	@Override
	public int delete(T bean){
		
		if(bean == null){
			return DB_EXECUTE_FAILED ;
		}
		
		final IAccessDatabase db = getDefaultDb();
		
		if(db == null){
			
			Log.e(TAG, "zhouwei : delete db is null");
			
			return DB_EXECUTE_FAILED ;
		}
		
		return db.deleteObject(bean) > -1 ? DB_EXECUTE_SUCCESS : DB_EXECUTE_FAILED;
	} 
	
	@Override
	public final void executeSql(String sql , String[] params){
		
		final IAccessDatabase db = getDefaultDb();
		
		if(db == null){
			
			Log.e(TAG, "zhouwei : executeSql db is null");
			
			return ;
		}
		
		db.execute(sql, params);
	}
}
