package com.sxlc.utils;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 获取手机当前时间
 *创建时间：2013-11-1下午3:46:32
 *@author 张涛
 *@since JDK1.6
 *@version 1.0
 */
public class Time {
	public static String getDateCN() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
		String date = format.format(new Date(System.currentTimeMillis()));
		return date;// 2012年10月03日 23:41:31
	}

	public static String getDateEN() {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date1 = format1.format(new Date(System.currentTimeMillis()));
		return date1;// 2012-10-03 23:41:31
	}

	public static String getDate() {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		String date = format.format(new Date(System.currentTimeMillis()));
		return date;
	}
}
