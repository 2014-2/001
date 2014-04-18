package com.crtb.tunnelmonitor.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class CrtbUtils {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static String formatSectionName(String pre, float value){
		
		String km = String.valueOf((int)(value / 1000));
		String m = String.valueOf((int)(value % 1000));
		
		return pre + km + "+" + m ;
	}
	
    public static Date parseDate(String text) {
        Date result = null;
        if (text == null || text.length() == 0) {
            throw new IllegalArgumentException("Date text is empty.");
        }
        try {
            result = DATE_FORMAT.parse(text);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }
	
}
