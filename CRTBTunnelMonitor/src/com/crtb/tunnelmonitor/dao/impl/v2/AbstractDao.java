package com.crtb.tunnelmonitor.dao.impl.v2;

import org.zw.android.framework.IAccessDatabase;
import org.zw.android.framework.impl.FrameworkFacade;

/**
 * abstract dao
 * 
 * @author zhouwei
 *
 */
public abstract class AbstractDao {

	protected IAccessDatabase 	mDatabase ;
	
	protected AbstractDao(){
		mDatabase	= FrameworkFacade.getFrameworkFacade().getAccessDatabase() ;
	}
	
	public final boolean insert(Object bean){
		
		if(bean == null){
			return false ;
		}
		
		return mDatabase.saveObject(bean) > -1 ;
	}
	
	public final boolean update(Object bean){
		
		if(bean == null){
			return false ;
		}
		
		return mDatabase.updateObject(bean) > -1 ;
	}
	
	public final boolean delete(Object bean){
		
		if(bean == null){
			return false ;
		}
		
		return mDatabase.deleteObject(bean) > -1 ;
	} 
}
