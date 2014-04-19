package com.crtb.tunnelmonitor.dao.impl.v2;

import org.zw.android.framework.IAccessDatabase;
import org.zw.android.framework.impl.FrameworkFacade;

import android.content.Context;

import com.crtb.tunnelmonitor.entity.CrtbUser;

/**
 * 权限认证
 * 
 * 1. 注册默认账号
 * 2. 注册授权账号
 * 3. 查询当前工作面
 * 4. 查询所有工作面
 * 
 * @author zhouwei
 *
 */
public final class CrtbLicenseDao {
	
	static final String TAG 		= "CrtbLicenseDao" ;

	private static CrtbLicenseDao	_instance ;
	
	private String 					mDefaultUsername ;
	private CrtbUser 				mCrtbUser ;
	private IAccessDatabase 		mDatabase ;
	
	private CrtbLicenseDao(){
		mDatabase	= FrameworkFacade.getFrameworkFacade().openDefaultDatabase() ;
	}
	
	public static CrtbLicenseDao defaultDao(){
		
		if(_instance == null){
			_instance	= new CrtbLicenseDao() ;
		}
		
		return _instance ;
	}
	
	public static void registDefaultUser(Context context){
		
		String username 		= context.getPackageName() ;
		
		CrtbLicenseDao dao 		= defaultDao() ;
		dao.mDefaultUsername	= username ;
		
		String sql = "select * from CrtbUser where username = ? " ;
		
		CrtbUser user = dao.mDatabase.queryObject(sql, new String[]{username}, CrtbUser.class);
		
		if(user == null){
			
			user = new CrtbUser() ;
			user.setUsername(username);
			user.setUsertype(1);
			user.setLicense("123456789");
			
			dao.mDatabase.saveObject(user);
		}
	}
	
	public void registLicense(Context context,String username,String license){
		
	}
	
	public CrtbUser queryCrtbUser(){
		
		//
		if(mCrtbUser == null){
			
			// 查询注册用户
			String sql = "select * from CrtbUser where username <> ? " ;
			
			// 注册用户
			CrtbUser user = mDatabase.queryObject(sql, new String[]{mDefaultUsername}, CrtbUser.class);
			
			if(user != null){
				mCrtbUser	= user ;
			} 
			// 默认注册用户
			else {
				sql = "select * from CrtbUser where username = ? " ;
				mCrtbUser	= mDatabase.queryObject(sql, new String[]{mDefaultUsername}, CrtbUser.class);
			}
		}
		
		return mCrtbUser ;
	}
	
}
