package com.crtb.tunnelmonitor.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppConfig {
	private static AppConfig sInstance;
	private static final String CONFIG_FILE_NAME = "app_config";
	private static final String KEY_SERVER_ADDRESS = "server_address";
	private static final String KEY_USER_NAME = "user_name";
	private static final String KEY_PASSWORD = "password";
	
	private SharedPreferences mPreferences;
	
	public static synchronized AppConfig getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new AppConfig(context);
		}
		return sInstance;
	}
	
			
	private AppConfig(Context context) {
		mPreferences = context.getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
	}
	
	public void setServerAddress(String address) {
		setString(KEY_SERVER_ADDRESS, address);
	}
	
	public String getServerAddress() {
		return getString(KEY_SERVER_ADDRESS, "");
	}
	
	public void setUserName(String userName) {
		setString(KEY_USER_NAME, userName);
	}
	
	public String getUserName() {
		return getString(KEY_USER_NAME, "");
	}
	
	public void setPassword(String password) {
		setString(KEY_PASSWORD, password);
	}
	
	public String getPassword() {
		return getString(KEY_PASSWORD, "");
	}
	
	private void setString(String key, String value) {
		Editor editor = mPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	private String getString(String key, String defValue) {
		return mPreferences.getString(key, defValue);
	}

}
