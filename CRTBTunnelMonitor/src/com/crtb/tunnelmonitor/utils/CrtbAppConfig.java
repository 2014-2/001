package com.crtb.tunnelmonitor.utils;

import com.crtb.tunnelmonitor.AppCRTBApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CrtbAppConfig {
	private static CrtbAppConfig sInstance;
	private static final String CONFIG_FILE_NAME = "app_config";
	private static final String KEY_SERVER_ADDRESS = "server_address";
	private static final String KEY_USER_NAME = "user_name";
	private static final String KEY_PASSWORD = "password";
	private static final String KEY_SECTION_SEQUENCE = "section_sequence";
	
	private SharedPreferences mPreferences;
	
	public static synchronized CrtbAppConfig getInstance() {
		if (sInstance == null) {
			sInstance = new CrtbAppConfig(AppCRTBApplication.getAppContext());
		}
		return sInstance;
	}
	
			
	private CrtbAppConfig(Context context) {
		mPreferences = context.getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
	}
	
	public void setServerAddress(String address) {
		setStringValue(KEY_SERVER_ADDRESS, address);
	}
	
	public String getServerAddress() {
		return getStringValue(KEY_SERVER_ADDRESS, "");
	}
	
	public void setUserName(String userName) {
		setStringValue(KEY_USER_NAME, userName);
	}
	
	public String getUserName() {
		return getStringValue(KEY_USER_NAME, "");
	}
	
	public void setPassword(String password) {
		setStringValue(KEY_PASSWORD, password);
	}
	
	public String getPassword() {
		return getStringValue(KEY_PASSWORD, "");
	}
	
	public int getSectionSequence() {
		return getIntValue(KEY_SECTION_SEQUENCE, 0);
	}
	
	public void setSectionSequence(int sequence) {
		setIntValue(KEY_SECTION_SEQUENCE, sequence);
	}
	
	private void setStringValue(String key, String value) {
		Editor editor = mPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	private String getStringValue(String key, String defValue) {
		return mPreferences.getString(key, defValue);
	}
	
	private void setIntValue(String key, int value) {
		Editor editor = mPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	private int getIntValue(String key, int defValue) {
		return mPreferences.getInt(key, defValue);
	}

}
