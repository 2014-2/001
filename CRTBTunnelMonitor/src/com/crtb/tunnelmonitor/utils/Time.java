package com.crtb.tunnelmonitor.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.TextUtils;


/**
 * 获取手机当前时间
 */
public class Time {
	private static final String DEFAULT_FORMAT = "YYYY-MM-DD HH:MM:SS";
	public static final long ONE_SECOND = 1000;
    public static final long DAY_MILLISECEND_RATIO = 24 * 60 * 60 * 1000;

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

    public static Date strToDate(String dateStr, String format) {
        if (!TextUtils.isEmpty(dateStr)) {
            if (TextUtils.isEmpty(format)) {
                format = DEFAULT_FORMAT;
            }
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date date = null;
            try {
                date = sdf.parse(dateStr);
                return date;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Timestamp strToTimeStamp(String dateStr, String format) {
        Date date = strToDate(dateStr, format);
        return date == null ? null : new Timestamp(date.getTime());
    }
}
