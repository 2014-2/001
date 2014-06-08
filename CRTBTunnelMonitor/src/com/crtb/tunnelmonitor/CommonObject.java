package com.crtb.tunnelmonitor;

import java.util.HashMap;

import org.zw.android.framework.util.StringUtils;

import android.os.Bundle;

public final class CommonObject {
	
	private static HashMap<String, Object> PrameterMap = new HashMap<String, Object>() ;

	private CommonObject(){
		
	}
	
	public static void reset(){
		PrameterMap.clear() ;
	}
	
	public static void putInteger(String key,int value){
		
		if(!StringUtils.isEmpty(key)){
			PrameterMap.put(key, value) ;
		}
	}
	
	public static void putDouble(String key,double value){
		
		if(!StringUtils.isEmpty(key)){
			PrameterMap.put(key, value) ;
		}
	}
	
	public static void putString(String key,String value){
		
		if(!StringUtils.isEmpty(key)){
			PrameterMap.put(key, value) ;
		}
	}
	
	public static void putObject(String key,Object obj){
		
		if(!StringUtils.isEmpty(key)){
			PrameterMap.put(key, obj) ;
		}
	}
	
	public static void remove(String key){
		
		if(!StringUtils.isEmpty(key)){
			PrameterMap.remove(key);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static final <T> T findObject(Bundle bundle, String key){
		
		if(bundle == null){
			return null ;
		}
		
		Object obj = bundle.getSerializable(key);
		
		try{
			return obj != null ? (T) obj : null;
		}catch(Exception e){
			e.printStackTrace() ;
		}
		
		return null ;
	}
	
	@SuppressWarnings("unchecked")
	public static  final <T> T findObject(String key){
		
		if(StringUtils.isEmpty(key)){
			return null ;
		}
		
		Object o = PrameterMap.get(key);
		
        if (o == null) {
            return null;
        }
        
        try {
            return (T) o;
        } catch (ClassCastException e) {
        	e.printStackTrace() ;
            return null;
        }
	}
	
	public static String findString(String key){
		
		if(StringUtils.isEmpty(key)){
			return null ;
		}
		
		Object o = PrameterMap.get(key);
		
        if (o == null) {
            return null;
        }
        
        try {
            return (String) o;
        } catch (ClassCastException e) {
        	e.printStackTrace() ;
            return null;
        }
	}
	
	public static double findDouble(String key){
		
		if(StringUtils.isEmpty(key)){
			return 0d ;
		}
		
		Object o = PrameterMap.get(key);
		
        if (o == null) {
            return 0d;
        }
        
        try {
            return (Double) o;
        } catch (ClassCastException e) {
        	e.printStackTrace() ;
            return 0d;
        }
	}
	
	public static int findInteger(String key){
		
		if(StringUtils.isEmpty(key)){
			return 0 ;
		}
		
		Object o = PrameterMap.get(key);
		
        if (o == null) {
            return 0;
        }
        
        try {
            return (Integer) o;
        } catch (ClassCastException e) {
        	e.printStackTrace() ;
            return 0;
        }
	}
}
