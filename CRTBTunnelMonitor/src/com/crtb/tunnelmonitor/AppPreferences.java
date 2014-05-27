package com.crtb.tunnelmonitor;

import org.zw.android.framework.util.StringUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * Android Preferences 存储
 * 
 * @author zhouwei
 *
 */
public final class AppPreferences {
	
	static final String KEY_CURRENT_PROJECT				= "_key_current_project" ; // 当前项目
	static final String KEY_CURRENT_PROJECT_GUID		= "_key_current_project_guid" ; // 当前项目guid
	
	private static AppPreferences _instance ;
	private SharedPreferences 	mSharedPreferences ;
	private Editor 				mEditor ;
	
	private AppPreferences(Context context){
		
		mSharedPreferences 	= PreferenceManager.getDefaultSharedPreferences(context);
		mEditor 			= mSharedPreferences.edit() ;
	}
	
	public static void initCrtbPreferences(Context context){
		
		if(context == null){
			return ;
		}
		
		if(_instance == null){
			_instance  = new AppPreferences(context) ;
		}
	}
	
	public static AppPreferences getPreferences(){ 
		return _instance ;
	}
	
	// 设置当前工程名称
	public void putCurrentProject(String projectName,String guid){
		
		if(!StringUtils.isEmpty(projectName)){
			
			putString(KEY_CURRENT_PROJECT, projectName);
			putString(KEY_CURRENT_PROJECT_GUID, guid);
		}
	}
	
	// 当前工程名称
	public String getCurrentProject(){
		return getString(KEY_CURRENT_PROJECT) ;
	}
	
	// 当前工作面guid
	public String getCurrentProjectGuid(){
		return getString(KEY_CURRENT_PROJECT_GUID) ;
	}
	
	public String getCurrentSimpleProjectName(){
		
		String name = getCurrentProject();
		
		if(StringUtils.isEmpty(name)){
			return "" ;
		}
		
		int start = name.indexOf("_");
		int end		= name.lastIndexOf(".");
		
		if(end > start){
			return name.substring(start + 1, end);
		}
		
		return "" ;
	}
	
	private boolean checkGetInput(String key){
		
		if(key == null || key.equals("")){
			return false ;
		}
		
		if(mSharedPreferences == null){
			return false ;
		}
		
		return true ;
	}
	
	private boolean checkPutInput(String key){
		
		if(key == null || key.equals("")){
			return false ;
		}
		
		if(mEditor == null){
			return false ;
		}
		
		return true ;
	}
	
	public String getString(String key){
		
		if(!checkGetInput(key)) return null ;
		
		return mSharedPreferences.getString(key, "");
	}
	
	public int getInt(String key){
		
		if(!checkGetInput(key)) return -1 ;
		
		return mSharedPreferences.getInt(key, -1);
	}
	
	public long getLong(String key){
		
		if(!checkGetInput(key)) return -1 ;
		
		return mSharedPreferences.getLong(key, -1) ;
	}
	
	public float getFloat(String key){
		
		if(!checkGetInput(key)) return -1 ;
		
		return mSharedPreferences.getFloat(key, 0.0f) ;
	}
	
	public boolean getBoolean(String key){
		
		if(!checkGetInput(key)) return false ;
		
		return mSharedPreferences.getBoolean(key, false) ;
	}
	
	public boolean getBoolean(String key,boolean defaultValue){
		
		if(!checkGetInput(key)) return false ;
		
		return mSharedPreferences.getBoolean(key, defaultValue) ;
	}
	
	public void putString(String key,String value){
		
		if(!checkPutInput(key) || value == null) return ;
		
		mEditor.putString(key, value).commit() ;
	}
	
	public void putInt(String key,int value){
		
		if(!checkPutInput(key)) return ;
		
		mEditor.putInt(key, value).commit() ;
	}
	
	public void putLong(String key,long value){
		
		if(!checkPutInput(key)) return ;
		
		mEditor.putLong(key, value).commit() ;
	}
	
	public void putBoolean(String key,boolean value){
		
		if(!checkPutInput(key)) return ;
		
		mEditor.putBoolean(key, value).commit() ;
	}
	
	public void putFloat(String key,float value){
		
		if(!checkPutInput(key)) return ;
		
		mEditor.putFloat(key, value).commit() ;
	}
	
	public void clear(){
		mSharedPreferences 	= null ;
		mEditor				= null ;
		_instance			= null ;
	}

}
