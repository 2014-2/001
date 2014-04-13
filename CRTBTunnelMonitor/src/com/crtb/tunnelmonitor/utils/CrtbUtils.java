package com.crtb.tunnelmonitor.utils;

public final class CrtbUtils {

	public static String formatSectionName(String pre, float value){
		
		String km = String.valueOf((int)(value / 1000));
		String m = String.valueOf((int)(value % 1000));
		
		return pre + km + "+" + m ;
	}
	
	
}
