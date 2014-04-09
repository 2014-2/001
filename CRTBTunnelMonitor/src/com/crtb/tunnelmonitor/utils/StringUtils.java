package com.crtb.tunnelmonitor.utils;

import java.util.Date;

/**
 * String Utils
 * 
 * @author zhouwei
 *
 */
public final class StringUtils {

	public static final String EMAIL_PATTERN 				= "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
	public static final String PHONE_PATTERN 				= "^(\\+?\\d+)?1[3456789]\\d{9}$";
	public static final String PASSWORD_PATTERN				= "^[a-zA-Z0-9_]{6,20}$" ;
	
	public static boolean isEmpty(String source) {

		if (source == null || "".equals(source))
			return true;

		for (int i = 0; i < source.length(); i++) {
			char c = source.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	public static boolean matches(String source, String pattern) {

		if (source == null || !source.matches(pattern)) {
			return false;
		}
		
		return true;
	}
	
	public static boolean isEmail(String source) {
		return matches(source, EMAIL_PATTERN);
	}

	public static boolean isPhone(String source) {
		return matches(source, PHONE_PATTERN);
	}
	
	public static boolean isPassword(String source){
		return matches(source, PASSWORD_PATTERN);
	}
	
	public static boolean isHasEmptyChar(String source){
		
		if(source == null){
			return true ;
		}
		
		return source.trim().indexOf(' ') >= 0 ;
	}

	public static String toNotNullString(String source) {
		return source == null ? "" : source;
	}
	
	public static String arrayToString(String[] array, String separator) {

		if (array == null || array.length == 0) {
			return "";
		}
		StringBuilder result = new StringBuilder();
		for (String temp : array) {
			result.append(temp).append(separator);
		}
		return result.substring(0, result.length() - 2);
	}

	public static String[] toArray(String source) {

		if (isEmpty(source)) {
			return null;
		}
		return source.split(",");
	}

	public static String[] toArray(Object[] sources) {

		if (sources == null || sources.length == 0) {
			return null;
		}
		String[] temps = new String[sources.length];
		String value = "";
		for (int i = 0; i < temps.length; i++) {

			if (sources[i] == null) {
				continue;
			}
			if (Date.class.equals(sources[i].getClass())) {
				value = DateUtils.toDateString((Date) sources[i],
						DateUtils.DATE_TIME_FORMAT);
			} else {
				value = sources[i].toString();
			}
			temps[i] = value;
		}
		return temps;
	}

	public static String capitalize(String str) {

		if (str == null || str.length() == 0) {
			return str;
		}
		
		StringBuilder sb = new StringBuilder(str.length());
		sb.append(Character.toUpperCase(str.charAt(0)));
		sb.append(str.substring(1));
		
		return sb.toString();
	}
}
